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
import com.fast.common.constant.DictConstants;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageRequest;
import com.fast.framework.annotation.DataScope;
import com.fast.framework.helper.ConfigHelper;
import com.fast.framework.helper.DictHelper;
import com.fast.framework.helper.MinioHelper;
import com.fast.framework.helper.RedisHelper;
import com.fast.framework.properties.MinioProperties;
import com.fast.modules.system.domain.dto.*;
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

            // 设置角色和锁定状态
            records.forEach(vo -> {
                vo.setRoles(roleMap.getOrDefault(vo.getId(), new ArrayList<>()));
                Boolean locked = RedisHelper.hasKey(RedisConstants.LOGIN_LOCK_PREFIX + vo.getUsername());
                vo.setLocked(locked);
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
        if (isAdmin(userId)) {
            throw new BusinessException("不能修改管理员用户");
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
        if (isAdmin(userId)) {
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
        if (isAdmin(userId)) {
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
        if (ids.stream().anyMatch(this::isAdmin)) {
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
     * 判断是否为管理员用户
     *
     * @param userId 用户 ID
     * @return 是否为管理员
     */
    private boolean isAdmin(Long userId) {
        return userId != null && userId == 1L;
    }
}