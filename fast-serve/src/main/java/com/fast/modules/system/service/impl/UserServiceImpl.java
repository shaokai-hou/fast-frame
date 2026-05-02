package com.fast.modules.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.ConfigConstants;
import com.fast.common.constant.Constants;
import com.fast.common.constant.DictConstants;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.framework.helper.AdminHelper;
import com.fast.common.result.PageRequest;
import com.fast.framework.annotation.DataScope;
import com.fast.framework.helper.ConfigHelper;
import com.fast.framework.helper.DictHelper;
import com.fast.framework.helper.MinioHelper;
import com.fast.framework.helper.RedisHelper;
import com.fast.framework.properties.MinioProperties;
import com.fast.modules.system.domain.dto.UserDTO;
import com.fast.modules.system.domain.dto.UserImportDTO;
import com.fast.modules.system.domain.query.UserQuery;
import com.fast.modules.system.domain.vo.FileVO;
import com.fast.modules.system.domain.vo.RoleVO;
import com.fast.modules.system.domain.vo.UserExportVO;
import com.fast.modules.system.domain.vo.UserSimpleVO;
import com.fast.modules.system.domain.vo.UserVO;
import com.fast.modules.system.domain.entity.Dept;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.mapper.UserMapper;
import com.fast.modules.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final RoleService roleService;
    private final FileService fileService;
    private final DeptService deptService;


    /**
     * 分页查询用户列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 用户分页结果
     */
    @Override
    @DataScope
    public IPage<UserVO> pageUsers(UserQuery query, PageRequest pageRequest) {
        IPage<UserVO> result = baseMapper.selectUserPage(pageRequest.toPage(), query);
        List<UserVO> records = result.getRecords();

        if (!records.isEmpty()) {
            // 批量查询角色（解决 N+1 问题）
            List<Long> userIds = records.stream().map(UserVO::getId).collect(Collectors.toList());
            List<RoleVO> allRoles = baseMapper.selectRolesByUserIds(userIds);

            // 按 userId 分组
            Map<Long, List<RoleVO>> roleMap = allRoles.stream()
                .collect(Collectors.groupingBy(RoleVO::getUserId));

            // 批量查询部门全路径
            Map<Long, String> deptFullNameMap = buildDeptFullNameMap(records);

            // 设置角色和锁定状态
            records.forEach(vo -> {
                vo.setRoles(roleMap.getOrDefault(vo.getId(), new ArrayList<>()));
                Boolean locked = RedisHelper.hasKey(RedisConstants.LOGIN_LOCK_PREFIX + vo.getUsername());
                vo.setLocked(locked);
                // 设置部门全路径
                vo.setDeptFullName(deptFullNameMap.get(vo.getDeptId()));
            });
        }
        return result;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    /**
     * 根据用户 ID 查询角色列表
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    @Override
    public List<RoleVO> listRolesByUserId(Long userId) {
        return roleService.listRolesByUserId(userId);
    }

    /**
     * 获取用户详情
     *
     * @param id 用户 ID
     * @return 用户信息 VO
     */
    @Override
    public UserVO getUserDetailById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = BeanUtil.copyProperties(user, UserVO.class);
        // 查询用户关联的角色 ID
        List<RoleVO> roles = roleService.listRolesByUserId(id);
        vo.setRoleIds(roles.stream().map(RoleVO::getId).collect(Collectors.toList()));
        return vo;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息 VO
     */
    @Override
    public UserVO getCurrentUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        return getUserDetailById(userId);
    }

    /**
     * 查询用户列表（用于导出）
     *
     * @param query    查询条件
     * @return 用户导出数据列表
     */
    @Override
    @DataScope
    public List<UserExportVO> listUserForExport(UserQuery query) {
        // 查询用户列表（不分页）
        List<UserVO> userVOList = baseMapper.selectUserList(query);
        // 转换为导出 VO，处理字典转换
        return userVOList.stream().map(userVO -> {
            UserExportVO exportVO = BeanUtil.copyProperties(userVO, UserExportVO.class);
            exportVO.setGender(DictHelper.getLabel(DictConstants.USER_SEX, userVO.getGender(), "未知"));
            exportVO.setStatus(DictHelper.getLabel(DictConstants.NORMAL_DISABLE, userVO.getStatus(), "正常"));
            return exportVO;
        }).collect(Collectors.toList());
    }


    /**
     * 新增用户
     *
     * @param dto 用户参数 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDTO dto) {
        // 检查用户名是否存在
        User existUser = getByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        // 检查是否选择管理员角色
        if (dto.getRoleIds() != null && Arrays.stream(dto.getRoleIds()).anyMatch(AdminHelper::isAdminRole)) {
            throw new BusinessException("不能选择管理员角色");
        }
        // 保存用户
        User user = BeanUtil.copyProperties(dto, User.class);
        // 前端未提供密码，使用初始化密码
        String password = dto.getPassword();
        if (StrUtil.isBlank(password)) {
            password = ConfigHelper.getValue(ConfigConstants.USER_INIT_PASSWORD);
            if (StrUtil.isBlank(password)) {
                throw new BusinessException("初始化密码未配置");
            }
        }
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        save(user);
        // 保存用户角色关联
        if (ArrayUtil.isNotEmpty(dto.getRoleIds())) {
            baseMapper.insertUserRole(user.getId(), Arrays.asList(dto.getRoleIds()));
        }
    }


    /**
     * 更新用户
     *
     * @param dto 用户参数 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO dto) {
        Long userId = dto.getId();
        // 管理员保护（admin 用户 ID 为特定值）
        if (AdminHelper.isAdmin(userId)) {
            throw new BusinessException("不能修改管理员用户");
        }
        // 检查是否选择管理员角色
        if (dto.getRoleIds() != null && Arrays.stream(dto.getRoleIds()).anyMatch(AdminHelper::isAdminRole)) {
            throw new BusinessException("不能选择管理员角色");
        }
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 检查用户名是否重复
        if (!user.getUsername().equals(dto.getUsername())) {
            User existUser = getByUsername(dto.getUsername());
            if (existUser != null) {
                throw new BusinessException("用户名已存在");
            }
        }
        // 更新用户
        BeanUtil.copyProperties(dto, user, "password");
        updateById(user);
        // 删除原有角色关联，保存新的
        baseMapper.deleteUserRoleByUserId(user.getId());
        if (ArrayUtil.isNotEmpty(dto.getRoleIds())) {
            baseMapper.insertUserRole(user.getId(), Arrays.asList(dto.getRoleIds()));
        }
    }

    /**
     * 重置用户密码
     *
     * @param userId 用户 ID
     */
    @Override
    public void resetPwd(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (AdminHelper.isAdmin(userId)) {
            throw new BusinessException("不能重置管理员密码");
        }
        // 从参数配置获取初始化密码
        String initPassword = ConfigHelper.getValue(ConfigConstants.USER_INIT_PASSWORD);
        if (StrUtil.isBlank(initPassword)) {
            throw new BusinessException("初始化密码错误");
        }
        user.setPassword(BCrypt.hashpw(initPassword, BCrypt.gensalt()));
        updateById(user);
    }

    /**
     * 更新用户状态
     *
     * @param userId 用户 ID
     * @param status 状态（0: 正常, 1: 禁用）
     */
    @Override
    public void updateStatus(Long userId, String status) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (AdminHelper.isAdmin(userId)) {
            throw new BusinessException("不能禁用管理员用户");
        }
        user.setStatus(status);
        updateById(user);
    }

    /**
     * 更新用户头像
     *
     * @param userId 用户 ID
     * @param avatar 头像路径
     */
    @Override
    public void updateAvatar(Long userId, String avatar) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setAvatar(avatar);
        updateById(user);
    }

    /**
     * 更新用户密码
     *
     * @param userId 用户 ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        updateById(user);
    }

    /**
     * 更新当前用户个人资料
     *
     * @param dto 用户参数 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentUserProfile(UserDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 更新允许修改的字段
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        updateById(user);
    }

    /**
     * 更新当前用户密码
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentUserPassword(String oldPassword, String newPassword) {
        Long userId = StpUtil.getLoginIdAsLong();
        updatePassword(userId, oldPassword, newPassword);
    }


    /**
     * 删除用户
     *
     * @param ids 用户 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(List<Long> ids) {
        if (ids.stream().anyMatch(AdminHelper::isAdmin)) {
            throw new BusinessException("不能删除管理员用户");
        }
        removeByIds(ids);
        // 删除用户角色关联
        ids.forEach(userId -> baseMapper.deleteUserRoleByUserId(userId));
    }


    /**
     * 上传用户头像
     *
     * @param file 上传的文件
     * @return 包含文件名的 Map
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        User user = getById(userId);

        // 删除旧头像
        if (StrUtil.isNotBlank(user.getAvatar())) {
            MinioHelper.delete(MinioProperties.BUCKET_TYPE_AVATAR, user.getAvatar());
        }

        // 上传新头像到头像桶
        FileVO fileVO = fileService.uploadFileToBucket(file, MinioProperties.BUCKET_TYPE_AVATAR);
        // 更新用户头像（只存储 objectName，不存储桶名前缀）
        updateAvatar(userId, fileVO.getFileName());
        // 返回完整信息
        Map<String, String> result = new HashMap<>();
        result.put("fileName", fileVO.getFileName());
        return result;
    }

    /**
     * 导入用户数据
     *
     * @param dataList 导入数据列表
     * @return 导入结果（成功数、失败数、失败原因）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importUsers(List<UserImportDTO> dataList) {
        int successCount = 0;
        List<String> errorMessages = new ArrayList<>();

        // 获取初始化密码
        String initPassword = ConfigHelper.getValue(ConfigConstants.USER_INIT_PASSWORD, "123456");

        for (int i = 0; i < dataList.size(); i++) {
            UserImportDTO dto = dataList.get(i);
            int rowNum = i + 2;
            try {
                // 检查用户名是否为空
                if (StrUtil.isBlank(dto.getUsername())) {
                    errorMessages.add("第 " + rowNum + " 行：用户名不能为空");
                    continue;
                }
                // 检查用户名是否已存在
                User existUser = getByUsername(dto.getUsername());
                if (existUser != null) {
                    errorMessages.add("第 " + rowNum + " 行：用户名「" + dto.getUsername() + "」已存在");
                    continue;
                }
                // 创建用户
                User user = new User();
                user.setUsername(dto.getUsername());
                user.setNickname(StrUtil.isBlank(dto.getNickname()) ? dto.getUsername() : dto.getNickname());
                user.setPhone(dto.getPhone());
                user.setEmail(dto.getEmail());
                user.setGender(DictHelper.getValue(DictConstants.USER_SEX, dto.getGender(), "0"));
                user.setStatus(DictHelper.getValue(DictConstants.NORMAL_DISABLE, dto.getStatus(), "0"));
                // 设置密码
                String password = StrUtil.isBlank(dto.getPassword()) ? initPassword : dto.getPassword();
                user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                // 设置部门
                if (StrUtil.isNotBlank(dto.getDeptName())) {
                    Long deptId = deptService.getDeptIdByName(dto.getDeptName());
                    if (deptId != null) {
                        user.setDeptId(deptId);
                    }
                }
                // 保存用户
                save(user);
                successCount++;
            } catch (Exception e) {
                errorMessages.add("第 " + rowNum + " 行：" + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("errorCount", dataList.size() - successCount);
        result.put("errorMessages", errorMessages);
        return result;
    }


    /**
     * 解锁用户（清除登录锁定）
     *
     * @param userId 用户ID
     */
    @Override
    public void unlockUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        RedisHelper.delete(RedisConstants.LOGIN_LOCK_PREFIX + user.getUsername());
    }

    /**
     * 获取用户列表（简化信息，用于选择器）
     *
     * @return 用户列表
     */
    @Override
    public List<UserSimpleVO> listUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, Constants.NORMAL)
               .select(User::getId, User::getNickname)
               .orderByAsc(User::getNickname);
        List<User> users = list(wrapper);
        return users.stream()
            .map(user -> {
                UserSimpleVO vo = new UserSimpleVO();
                vo.setId(user.getId());
                vo.setNickname(user.getNickname());
                return vo;
            })
            .collect(Collectors.toList());
    }

    /**
     * 批量构建部门全路径名称
     *
     * @param userVOList 用户列表
     * @return 部门ID -> 部门全路径名称映射
     */
    private Map<Long, String> buildDeptFullNameMap(List<UserVO> userVOList) {
        // 收集所有部门ID
        Set<Long> deptIds = userVOList.stream()
            .map(UserVO::getDeptId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (deptIds.isEmpty()) {
            return new HashMap<>();
        }

        // 批量查询部门信息
        List<Dept> depts = deptService.listByIds(deptIds);
        Map<Long, Dept> deptMap = depts.stream()
            .collect(Collectors.toMap(Dept::getId, d -> d));

        // 收集 ancestors 中涉及的所有部门ID
        Set<Long> allDeptIds = new HashSet<>(deptIds);
        for (Dept dept : depts) {
            if (StrUtil.isNotBlank(dept.getAncestors())) {
                String[] ancestorIds = dept.getAncestors().split(",");
                for (String idStr : ancestorIds) {
                    if (!"0".equals(idStr)) {
                        allDeptIds.add(Long.parseLong(idStr));
                    }
                }
            }
        }

        // 批量查询所有涉及的部门（包括祖先）
        Map<Long, Dept> allDeptMap = new HashMap<>();
        if (!allDeptIds.isEmpty()) {
            List<Dept> allDepts = deptService.listByIds(allDeptIds);
            allDeptMap = allDepts.stream()
                .collect(Collectors.toMap(Dept::getId, d -> d));
        }

        // 构建部门全路径
        Map<Long, String> result = new HashMap<>();
        for (Long deptId : deptIds) {
            Dept dept = deptMap.get(deptId);
            if (dept != null) {
                String fullName = buildFullName(dept, allDeptMap);
                result.put(deptId, fullName);
            }
        }
        return result;
    }

    /**
     * 构建单个部门的全路径名称
     *
     * @param dept 部门
     * @param deptMap 部门ID映射（包含所有祖先）
     * @return 全路径名称（如：深圳分公司/研发部）
     */
    private String buildFullName(Dept dept, Map<Long, Dept> deptMap) {
        List<String> names = new ArrayList<>();
        // 添加当前部门名称
        names.add(dept.getDeptName());

        // 根据 ancestors 添加祖先部门名称
        if (StrUtil.isNotBlank(dept.getAncestors())) {
            String[] ancestorIds = dept.getAncestors().split(",");
            // ancestors 格式：0,1,2 表示祖先链（从根到父）
            // 需要按顺序添加：根 -> ... -> 父 -> 当前
            for (String idStr : ancestorIds) {
                if (!"0".equals(idStr)) {
                    Long ancestorId = Long.parseLong(idStr);
                    Dept ancestor = deptMap.get(ancestorId);
                    if (ancestor != null) {
                        names.add(0, ancestor.getDeptName()); // 在头部插入
                    }
                }
            }
        }
        return names.stream().collect(Collectors.joining("/"));
    }
}