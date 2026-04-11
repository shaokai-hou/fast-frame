package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fast.modules.system.entity.Role;
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
     * 删除角色菜单关联
     */
    void deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单关联
     */
    void insertRoleMenu(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    /**
     * 删除角色部门关联
     */
    void deleteRoleDeptByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色部门关联
     */
    void insertRoleDept(@Param("roleId") Long roleId, @Param("deptIds") List<Long> deptIds);

    /**
     * 根据用户ID查询角色列表
     */
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
}