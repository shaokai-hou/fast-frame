package com.fast.modules.flow.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fast.modules.system.domain.entity.Role;
import com.fast.modules.system.mapper.RoleMapper;
import com.fast.modules.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.dromara.warm.flow.core.handler.PermissionHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Warm-Flow 办理人权限处理器
 * 集成 Sa-Token 认证体系
 *
 * @author fast-frame
 */
@Component
@RequiredArgsConstructor
public class FlowPermissionHandler implements PermissionHandler {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    /**
     * 获取当前用户权限标识
     * 返回格式: 用户ID, role:角色Key
     *
     * @return 权限标识列表
     */
    @Override
    public List<String> permissions() {
        List<String> permissions = new ArrayList<>();

        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            permissions.add(userId);

            List<String> roleList = StpUtil.getRoleList();
            for (String roleKey : roleList) {
                permissions.add("role:" + roleKey);
            }
        }

        return permissions;
    }

    /**
     * 获取当前办理人（用户 ID）
     *
     * @return 当前办理人标识
     */
    @Override
    public String getHandler() {
        if (StpUtil.isLogin()) {
            return StpUtil.getLoginIdAsString();
        }
        return null;
    }

    /**
     * 转换权限标识为用户 ID 列表
     * 支持：用户ID、role:角色Key、dept:部门ID
     *
     * @param permissions 权限标识列表
     * @return 用户 ID 列表（字符串形式，适配 Warm-Flow）
     */
    @Override
    public List<String> convertPermissions(List<String> permissions) {
        List<String> userIds = new ArrayList<>();
        if (permissions == null) {
            return userIds;
        }

        for (String permission : permissions) {
            if (permission.startsWith("role:")) {
                // role:admin -> 查询该角色下的用户
                String roleKey = permission.substring(5);
                Role role = roleMapper.selectOne(
                    new LambdaQueryWrapper<Role>()
                        .eq(Role::getRoleKey, roleKey)
                );
                if (role != null) {
                    List<Long> roleUserIds = userMapper.selectUserIdsByRoleId(role.getId());
                    userIds.addAll(roleUserIds.stream().map(String::valueOf).collect(Collectors.toList()));
                }
            } else if (permission.startsWith("dept:")) {
                // dept:103 -> 查询该部门下的用户
                Long deptId = Long.parseLong(permission.substring(5));
                List<Long> deptUserIds = userMapper.selectUserIdsByDeptId(deptId);
                userIds.addAll(deptUserIds.stream().map(String::valueOf).collect(Collectors.toList()));
            } else {
                // 直接是用户 ID
                userIds.add(permission);
            }
        }

        return userIds;
    }
}