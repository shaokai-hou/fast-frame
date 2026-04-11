package com.fast.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.system.dto.UserDTO;
import com.fast.modules.system.dto.UserImportDTO;
import com.fast.modules.system.entity.User;
import com.fast.modules.system.vo.RoleVO;
import com.fast.modules.system.vo.UserExportVO;
import com.fast.modules.system.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户Service
 *
 * @author fast-frame
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户列表
     *
     * @param dto 查询参数
     * @return 用户分页结果
     */
    PageResult<UserVO> listUserPage(UserDTO dto);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User getByUsername(String username);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> listRolesByUserId(Long userId);

    /**
     * 新增用户
     *
     * @param dto 用户参数
     */
    void addUser(UserDTO dto);

    /**
     * 修改用户
     *
     * @param dto 用户参数
     */
    void updateUser(UserDTO dto);

    /**
     * 删除用户
     *
     * @param ids 用户ID列表
     */
    void deleteUser(List<Long> ids);

    /**
     * 重置密码（重置为系统初始化密码）
     *
     * @param userId 用户ID
     */
    void resetPwd(Long userId);

    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void updateStatus(Long userId, String status);

    /**
     * 更新用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像URL
     */
    void updateAvatar(Long userId, String avatar);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 根据ID获取用户详情（不包含密码，包含角色ID）
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserVO getUserDetailById(Long id);

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 更新当前登录用户个人资料
     *
     * @param dto 用户参数
     */
    void updateCurrentUserProfile(UserDTO dto);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像信息
     */
    Map<String, String> uploadAvatar(MultipartFile file);

    /**
     * 修改当前用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updateCurrentUserPassword(String oldPassword, String newPassword);

    /**
     * 查询用户列表（用于导出）
     *
     * @param dto 查询参数
     * @return 用户导出数据列表
     */
    List<UserExportVO> listUserForExport(UserDTO dto);

    /**
     * 导入用户数据
     *
     * @param dataList 导入数据列表
     * @return 导入结果（成功数、失败数、失败原因）
     */
    Map<String, Object> importUsers(List<UserImportDTO> dataList);
}