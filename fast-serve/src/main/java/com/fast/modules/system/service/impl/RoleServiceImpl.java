package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.Constants;
import com.fast.common.exception.BusinessException;
import com.fast.common.result.PageRequest;
import com.fast.modules.system.domain.dto.RoleDTO;
import com.fast.modules.system.domain.query.RoleQuery;
import com.fast.modules.system.domain.vo.RoleVO;
import com.fast.modules.system.domain.entity.Role;
import com.fast.modules.system.mapper.RoleMapper;
import com.fast.modules.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现
 *
 * @author fast-frame
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    /**
     * 分页查询角色列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 角色分页结果
     */
    @Override
    public IPage<RoleVO> pageRoles(RoleQuery query, PageRequest pageRequest) {
        return baseMapper.selectRolePage(pageRequest.toPage(), query);
    }

    /**
     * 查询所有角色列表
     *
     * @return 角色列表
     */
    @Override
    public List<RoleVO> listRoles() {
        List<Role> roles = baseMapper.selectList(Wrappers.emptyWrapper());
        return BeanUtil.copyToList(roles,RoleVO.class);
    }

    /**
     * 根据用户 ID 获取角色列表
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    @Override
    public List<RoleVO> listRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    /**
     * 根据用户 ID 获取角色标识列表
     *
     * @param userId 用户 ID
     * @return 角色标识列表
     */
    @Override
    public List<String> listRoleKeysByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId).stream()
                .map(RoleVO::getRoleKey)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * 新增角色
     *
     * @param dto 角色参数 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(RoleDTO dto) {
        // 检查角色名称是否存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, dto.getRoleName());
        if (count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        // 检查角色权限字符串是否存在
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, dto.getRoleKey());
        if (count(wrapper) > 0) {
            throw new BusinessException("角色权限字符串已存在");
        }
        // 保存角色
        Role role = BeanUtil.copyProperties(dto, Role.class);
        save(role);
        // 保存角色菜单关联
        if (ArrayUtil.isNotEmpty(dto.getMenuIds())) {
            baseMapper.insertRoleMenu(role.getId(), Arrays.asList(dto.getMenuIds()));
        }
        // 保存角色部门关联（自定义数据权限时）
        if (Constants.DATA_SCOPE_CUSTOM.equals(dto.getDataScope()) && ArrayUtil.isNotEmpty(dto.getDeptIds())) {
            baseMapper.insertRoleDept(role.getId(), Arrays.asList(dto.getDeptIds()));
        }
    }


    /**
     * 更新角色
     *
     * @param dto 角色参数 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDTO dto) {
        Long roleId = dto.getId();
        // 管理员角色保护
        if (isAdminRole(roleId)) {
            throw new BusinessException("不能修改管理员角色");
        }
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        // 检查角色名称是否重复
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleName, dto.getRoleName())
                .ne(Role::getId, dto.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }
        // 检查角色权限字符串是否重复
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleKey, dto.getRoleKey())
                .ne(Role::getId, dto.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException("角色权限字符串已存在");
        }
        // 更新角色
        BeanUtil.copyProperties(dto, role);
        updateById(role);
        // 删除原有菜单关联，保存新的
        baseMapper.deleteRoleMenuByRoleId(role.getId());
        if (ArrayUtil.isNotEmpty(dto.getMenuIds())) {
            baseMapper.insertRoleMenu(role.getId(), Arrays.asList(dto.getMenuIds()));
        }
        // 删除原有部门关联，保存新的
        baseMapper.deleteRoleDeptByRoleId(role.getId());
        if ("2".equals(dto.getDataScope()) && ArrayUtil.isNotEmpty(dto.getDeptIds())) {
            baseMapper.insertRoleDept(role.getId(), Arrays.asList(dto.getDeptIds()));
        }
    }

    /**
     * 删除角色
     *
     * @param ids 角色 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(List<Long> ids) {
        if (ids.stream().anyMatch(this::isAdminRole)) {
            throw new BusinessException("不能删除管理员角色");
        }
        // 检查角色是否已分配给用户
        for (Long roleId : ids) {
            if (baseMapper.countUsersByRoleId(roleId) > 0) {
                throw new BusinessException("角色已分配给用户，不能删除");
            }
        }
        removeByIds(ids);
        // 删除角色菜单和部门关联
        ids.forEach(roleId -> {
            baseMapper.deleteRoleMenuByRoleId(roleId);
            baseMapper.deleteRoleDeptByRoleId(roleId);
        });
    }

    /**
     * 判断是否为管理员角色
     *
     * @param roleId 角色 ID
     * @return 是否为管理员角色
     */
    private boolean isAdminRole(Long roleId) {
        return roleId != null && roleId == 1L;
    }
}