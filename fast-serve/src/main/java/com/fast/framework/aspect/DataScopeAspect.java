package com.fast.framework.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.fast.framework.annotation.DataScope;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.service.DeptService;
import com.fast.modules.system.service.RoleService;
import com.fast.modules.system.service.UserService;
import com.fast.modules.system.domain.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据权限切面
 *
 * @author fast-frame
 */
@Slf4j
@Aspect
@Component
public class DataScopeAspect {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private DeptService deptService;

    /**
     * 合法字段名正则（只允许字母、数字、下划线）
     */
    private static final Pattern VALID_FIELD_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    /**
     * SQL 最大长度限制（防止超长 SQL 拼接攻击）
     */
    private static final int MAX_SQL_LENGTH = 500;

    /**
     * 数据权限过滤
     *
     * @param point     切点
     * @param dataScope 数据权限注解
     */
    @Before("@annotation(dataScope)")
    public void doBefore(JoinPoint point, DataScope dataScope) {
        // 获取当前用户
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            return;
        }

        // 获取当前用户的角色列表
        List<RoleVO> roles = roleService.listRolesByUserId(userId);
        if (roles.isEmpty()) {
            return;
        }

        // 验证 userAlias 参数是否合法
        String userAlias = dataScope.userAlias();
        if (!VALID_FIELD_PATTERN.matcher(userAlias).matches()) {
            log.warn("[DataScope] 非法的 userAlias: {}, userId: {}", userAlias, userId);
            return;
        }

        // 构建数据权限SQL
        StringBuilder sqlString = new StringBuilder();
        for (RoleVO role : roles) {
            String dataScopeValue = role.getDataScope();
            if (dataScopeValue == null) {
                dataScopeValue = "1"; // 默认全部数据
            }

            switch (dataScopeValue) {
                case "1": // 全部数据权限
                    // 无需过滤
                    return;
                case "2": // 自定义数据权限
                    List<Long> deptIds = deptService.getDeptIdsByRoleId(role.getId());
                    if (!deptIds.isEmpty() && validateIds(deptIds)) {
                        sqlString.append(String.format(
                                " OR %s.dept_id IN (%s) ",
                                userAlias,
                                deptIds.stream().map(String::valueOf).collect(Collectors.joining(","))
                        ));
                    }
                    break;
                case "3": // 本部门数据权限
                    if (user.getDeptId() != null) {
                        sqlString.append(String.format(" OR %s.dept_id = %d ", userAlias, user.getDeptId()));
                    }
                    break;
                case "4": // 本部门及以下数据权限
                    List<Long> deptAndChildrenIds = deptService.getDeptAndChildrenIds(user.getDeptId());
                    if (!deptAndChildrenIds.isEmpty() && validateIds(deptAndChildrenIds)) {
                        sqlString.append(String.format(
                                " OR %s.dept_id IN (%s) ",
                                userAlias,
                                deptAndChildrenIds.stream().map(String::valueOf).collect(Collectors.joining(","))
                        ));
                    }
                    break;
                case "5": // 仅本人数据权限
                    sqlString.append(String.format(" OR %s.id = %d ", userAlias, userId));
                    break;
                default:
                    break;
            }
        }

        // 设置数据权限参数
        if (sqlString.length() > 0) {
            Object params = point.getArgs().length > 0 ? point.getArgs()[0] : null;
            if (params != null) {
                try {
                    // 通过反射设置 dataScope 参数
                    java.lang.reflect.Field field = params.getClass().getDeclaredField("dataScope");
                    field.setAccessible(true);
                    String dataScopeSql = " AND (" + sqlString.substring(4) + ")";
                    // 验证生成的 SQL 格式
                    if (validateDataScopeSql(dataScopeSql)) {
                        field.set(params, dataScopeSql);
                    } else {
                        log.warn("[DataScope] 生成的 SQL 格式异常: {}, userId: {}", dataScopeSql, userId);
                    }
                } catch (Exception e) {
                    // 忽略异常，参数可能没有 dataScope 字段
                }
            }
        }
    }

    /**
     * 验证 ID 列表是否都是有效的正整数
     *
     * @param ids ID 列表
     * @return 是否有效
     */
    private boolean validateIds(List<Long> ids) {
        return ids.stream().allMatch(id -> id != null && id > 0);
    }

    /**
     * 验证生成的 SQL 格式是否合法
     * 只允许包含 dept_id、id 字段和 IN、= 操作符
     *
     * @param sql 生成的 SQL
     * @return 是否合法
     */
    private boolean validateDataScopeSql(String sql) {
        // 长度限制：防止超长 SQL 拼接攻击
        if (sql == null || sql.length() > MAX_SQL_LENGTH) {
            log.warn("[DataScope] SQL 长度超限或为空: length={}, userId: {}", sql == null ? 0 : sql.length(), StpUtil.getLoginIdAsLong());
            return false;
        }
        // 基本格式验证：只允许特定格式
        Pattern pattern = Pattern.compile(
                "^\\s*AND\\s+\\([a-zA-Z_]+\\.(dept_id|id)\\s+(IN\\s*\\([\\d,]+\\)|=\\s*\\d+)\\s*(OR\\s+[a-zA-Z_]+\\.(dept_id|id)\\s+(IN\\s*\\([\\d,]+\\)|=\\s*\\d+))*\\s*\\)$",
                Pattern.CASE_INSENSITIVE
        );
        return pattern.matcher(sql).matches();
    }
}