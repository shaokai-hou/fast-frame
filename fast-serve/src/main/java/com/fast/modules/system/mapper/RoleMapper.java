package com.fast.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.system.domain.query.RoleQuery;
import com.fast.modules.system.domain.vo.RoleVO;
import com.fast.modules.system.domain.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色Mapper
 *
 * @author fast-frame
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 分页查询角色列表
     *
     * @param page  分页对象
     * @param query 查询参数
     * @return 角色分页结果
     */
    IPage<RoleVO> selectRolePage(IPage<RoleVO> page, RoleQuery query);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> selectRolesByUserId(Long userId);

    /**
     * 批量插入角色菜单关联
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void insertRoleMenu(Long roleId, List<Long> menuIds);

    /**
     * 批量插入角色部门关联
     *
     * @param roleId  角色ID
     * @param deptIds 部门ID列表
     */
    void insertRoleDept(Long roleId, List<Long> deptIds);

    /**
     * 删除角色菜单关联
     *
     * @param roleId 角色ID
     */
    void deleteRoleMenuByRoleId(Long roleId);

    /**
     * 删除角色部门关联
     *
     * @param roleId 角色ID
     */
    void deleteRoleDeptByRoleId(Long roleId);

    /**
     * 检查角色是否已分配给用户
     *
     * @param roleId 角色ID
     * @return 使用该角色的用户数量
     */
    int countUsersByRoleId(Long roleId);
}