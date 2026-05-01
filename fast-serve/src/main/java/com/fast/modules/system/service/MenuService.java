package com.fast.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fast.modules.system.domain.vo.MenuTreeVO;
import com.fast.modules.system.domain.vo.MenuVO;
import com.fast.modules.system.domain.entity.Menu;

import java.util.List;

/**
 * 菜单Service
 *
 * @author fast-frame
 */
public interface MenuService extends IService<Menu> {

    /**
     * 查询菜单树
     *
     * @return 菜单树列表
     */
    List<MenuVO> listMenuTree();

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<MenuVO> listMenusByUserId(Long userId);

    /**
     * 查询菜单树(用于选择器)
     *
     * @return 菜单树选择器数据
     */
    List<MenuTreeVO> listMenuTreeSelect();

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> listMenuIdsByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> listPermissionsByUserId(Long userId);

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     */
    void addMenu(Menu menu);

    /**
     * 修改菜单
     *
     * @param menu 菜单信息
     */
    void updateMenu(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    void deleteMenu(Long id);
}