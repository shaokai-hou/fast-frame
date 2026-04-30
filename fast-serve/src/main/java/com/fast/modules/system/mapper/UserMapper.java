package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.dto.RoleVO;
import com.fast.modules.system.domain.dto.UserQuery;
import com.fast.modules.system.domain.dto.UserVO;
import com.fast.modules.system.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表
     *
     * @param page 分页对象
     * @param dto  查询参数DTO
     * @return 用户分页结果
     */
    IPage<UserVO> selectUserPage(IPage<UserVO> page, UserQuery query);

    /**
     * 查询用户列表（不分页，用于导出）
     *
     * @param query 查询参数
     * @return 用户列表
     */
    List<UserVO> selectUserList(UserQuery query);

    /**
     * 批量插入用户角色关联
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void insertUserRole(Long userId, List<Long> roleIds);

    /**
     * 删除用户角色关联
     *
     * @param userId 用户ID
     */
    void deleteUserRoleByUserId(Long userId);

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByRoleId(Long roleId);

    /**
     * 根据部门ID查询用户ID列表
     *
     * @param deptId 部门ID
     * @return 用户ID列表
     */
    List<Long> selectUserIdsByDeptId(Long deptId);

    /**
     * 批量查询用户角色
     *
     * @param userIds 用户ID列表
     * @return 角色列表（包含userId字段用于关联）
     */
    List<RoleVO> selectRolesByUserIds(List<Long> userIds);
}