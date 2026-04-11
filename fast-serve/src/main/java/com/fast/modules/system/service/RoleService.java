package com.fast.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageResult;
import com.fast.modules.system.dto.RoleDTO;
import com.fast.modules.system.entity.Role;
import com.fast.modules.system.vo.RoleVO;

import java.util.List;

/**
 * 角色Service
 *
 * @author fast-frame
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     *
     * @param dto 查询参数
     * @return 角色分页结果
     */
    PageResult<RoleVO> listRolePage(RoleDTO dto);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<RoleVO> listAllRoles();

    /**
     * 新增角色
     *
     * @param dto 角色参数
     */
    void addRole(RoleDTO dto);

    /**
     * 修改角色
     *
     * @param dto 角色参数
     */
    void updateRole(RoleDTO dto);

    /**
     * 删除角色
     *
     * @param ids 角色ID列表
     */
    void deleteRole(List<Long> ids);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> listRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色实体列表
     *
     * @param userId 用户ID
     * @return 角色实体列表
     */
    List<Role> listRoleEntitiesByUserId(Long userId);
}