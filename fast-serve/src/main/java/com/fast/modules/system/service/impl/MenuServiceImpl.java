package com.fast.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fast.common.constant.Constants;
import com.fast.common.exception.BusinessException;
import com.fast.modules.system.domain.vo.MenuTreeVO;
import com.fast.modules.system.domain.vo.MenuVO;
import com.fast.modules.system.domain.entity.Menu;
import com.fast.modules.system.domain.entity.User;
import com.fast.modules.system.mapper.MenuMapper;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 菜单Service实现
 *
 * @author fast-frame
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final UserService userService;

    private static final String ADMIN_USERNAME = "admin";

    public MenuServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * 判断用户是否为超级管理员
     *
     * @param userId 用户ID
     * @return 是否为超级管理员
     */
    private boolean isAdmin(Long userId) {
        User user = userService.getById(userId);
        return user != null && ADMIN_USERNAME.equals(user.getUsername());
    }


    /**
     * 查询菜单树形列表（用于菜单管理，包含禁用菜单）
     *
     * @return 菜单树形列表
     */
    @Override
    public List<MenuVO> listMenuTree() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getMenuSort)
               .orderByAsc(Menu::getCreateTime);
        List<Menu> menus = list(wrapper);
        return buildMenuTree(menus, 0L);
    }

    /**
     * 查询正常状态的菜单树形列表（用于路由）
     *
     * @return 菜单树形列表
     */
    private List<MenuVO> listActiveMenuTree() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getStatus, Constants.NORMAL)
               .orderByAsc(Menu::getMenuSort)
               .orderByAsc(Menu::getCreateTime);
        List<Menu> menus = list(wrapper);
        return buildMenuTree(menus, 0L);
    }

    /**
     * 根据用户 ID 查询菜单列表
     *
     * @param userId 用户 ID
     * @return 菜单树形列表
     */
    @Override
    public List<MenuVO> listMenusByUserId(Long userId) {
        // admin 用户返回所有正常菜单
        if (isAdmin(userId)) {
            return listActiveMenuTree();
        }
        List<Menu> menus = baseMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus, 0L);
    }

    /**
     * 获取菜单树形选择器列表
     *
     * @return 菜单树形选择器列表
     */
    @Override
    public List<MenuTreeVO> listMenuTreeSelect() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Menu::getMenuSort);
        List<Menu> menus = list(wrapper);
        return buildTreeSelect(menus, 0L);
    }

    /**
     * 根据角色 ID 获取菜单 ID 列表
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        return baseMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 根据用户 ID 查询权限标识列表
     *
     * @param userId 用户 ID
     * @return 权限标识列表
     */
    @Override
    public List<String> listPermissionsByUserId(Long userId) {
        // admin 用户直接返回超级权限
        if (isAdmin(userId)) {
            return Collections.singletonList("*:*:*");
        }
        return baseMapper.selectPermissionsByUserId(userId);
    }


    /**
     * 新增菜单
     *
     * @param menu 菜单实体
     */
    @Override
    public void addMenu(Menu menu) {
        // 检查菜单名称是否存在
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getMenuName, menu.getMenuName())
               .eq(Menu::getParentId, menu.getParentId());
        if (count(wrapper) > 0) {
            throw new BusinessException("同级菜单名称已存在");
        }
        save(menu);
    }


    /**
     * 更新菜单
     *
     * @param menu 菜单实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Menu menu) {
        Menu existMenu = getById(menu.getId());
        if (existMenu == null) {
            throw new BusinessException("菜单不存在");
        }
        // 检查菜单名称是否重复
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getMenuName, menu.getMenuName())
               .eq(Menu::getParentId, menu.getParentId())
               .ne(Menu::getId, menu.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException("同级菜单名称已存在");
        }
        // 不能将父菜单设置为自己
        if (menu.getId().equals(menu.getParentId())) {
            throw new BusinessException("父菜单不能是自己");
        }
        updateById(menu);
    }


    /**
     * 删除菜单
     *
     * @param id 菜单 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        // 检查是否存在子菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, id);
        if (count(wrapper) > 0) {
            throw new BusinessException("存在子菜单，不能删除");
        }
        removeById(id);
        // 删除角色菜单关联
        baseMapper.deleteRoleMenuByMenuId(id);
    }


    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @param parentId 父级 ID
     * @return 菜单树形列表
     */
    private List<MenuVO> buildMenuTree(List<Menu> menus, Long parentId) {
        List<MenuVO> result = new ArrayList<>();
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuVO vo = BeanUtil.copyProperties(menu, MenuVO.class);
                vo.setChildren(buildMenuTree(menus, menu.getId()));
                result.add(vo);
            }
        }
        return result.isEmpty() ? null : result;
    }

    /**
     * 构建树形选择器
     *
     * @param menus 菜单列表
     * @param parentId 父级 ID
     * @return 树形选择器列表
     */
    private List<MenuTreeVO> buildTreeSelect(List<Menu> menus, Long parentId) {
        List<MenuTreeVO> result = new ArrayList<>();
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                MenuTreeVO vo = new MenuTreeVO();
                vo.setId(menu.getId());
                vo.setParentId(menu.getParentId());
                vo.setLabel(menu.getMenuName());
                vo.setChildren(buildTreeSelect(menus, menu.getId()));
                result.add(vo);
            }
        }
        return result.isEmpty() ? null : result;
    }
}