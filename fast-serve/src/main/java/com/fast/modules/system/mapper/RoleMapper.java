package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.system.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {


    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);


    /**
     * 批量插入角色菜单关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    /**
     * 批量插入角色部门关联
     *
     * @param roleId  角色ID
     * @param deptIds 部门ID列表
     */
    void insertRoleDept(@Param("roleId") Long roleId, @Param("deptIds") List<Long> deptIds);


    /**
     * 删除角色菜单关联
     *
     * @param roleId 角色ID
     */
    void deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色部门关联
     *
     * @param roleId 角色ID
     */
    void deleteRoleDeptByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查角色是否已分配给用户
     *
     * @param roleId 角色ID
     * @return 使用该角色的用户数量
     */
    int countUsersByRoleId(@Param("roleId") Long roleId);
}