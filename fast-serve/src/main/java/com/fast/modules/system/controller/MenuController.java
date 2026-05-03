package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.dto.StatusUpdateDTO;
import com.fast.modules.system.domain.vo.MenuTreeVO;
import com.fast.modules.system.domain.vo.MenuVO;
import com.fast.modules.system.domain.entity.Menu;
import com.fast.modules.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class MenuController extends BaseController {

    private final MenuService menuService;

    /**
     * 查询菜单树
     *
     * @return 菜单树列表
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public Result<List<MenuVO>> list() {
        return success(menuService.listMenuTree());
    }

    /**
     * 根据ID获取菜单详情
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    @SaCheckPermission("system:menu:detail")
    @GetMapping("/{id}")
    public Result<Menu> getInfo(@PathVariable Long id) {
        return success(menuService.getById(id));
    }

    /**
     * 获取菜单树选择器
     *
     * @return 菜单树选择器数据
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        return success(menuService.listMenuTreeSelect());
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 成功结果
     */
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody Menu menu) {
        menuService.addMenu(menu);
        return success();
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单信息
     * @return 成功结果
     */
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody Menu menu) {
        menuService.updateMenu(menu);
        return success();
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 成功结果
     */
    @SaCheckPermission("system:menu:delete")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return success();
    }

    /**
     * 修改菜单状态
     *
     * @param dto 状态参数
     * @return 成功结果
     */
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@Validated @RequestBody StatusUpdateDTO dto) {
        Menu menu = new Menu();
        menu.setId(dto.getId());
        menu.setStatus(dto.getStatus());
        menuService.updateById(menu);
        return success();
    }
}