package com.fast.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.RoleDTO;
import com.fast.modules.system.domain.dto.RoleQuery;
import com.fast.modules.system.domain.dto.RoleVO;
import com.fast.modules.system.domain.entity.Role;

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
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 角色分页结果
     */
    IPage<RoleVO> pageRoles(RoleQuery query, PageRequest pageRequest);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<RoleVO> listRoles();

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleVO> listRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色标识列表
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    List<String> listRoleKeysByUserId(Long userId);

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
}