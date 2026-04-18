package com.fast.modules.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.PageRequest;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.domain.dto.MenuTreeVO;
import com.fast.modules.system.domain.dto.RoleDTO;
import com.fast.modules.system.domain.dto.RoleQuery;
import com.fast.modules.system.domain.dto.RoleVO;
import com.fast.modules.system.domain.entity.Role;
import com.fast.modules.system.service.MenuService;
import com.fast.modules.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private final RoleService roleService;
    private final MenuService menuService;

    /**
     * 分页查询角色列表
     *
     * @param query    查询条件
     * @param pageRequest 分页参数
     * @return 角色分页结果
     */
    @SaCheckPermission("system:role:page")
    @GetMapping("/page")
    public Result<IPage<RoleVO>> page(RoleQuery query, PageRequest pageRequest) {
        return success(roleService.pageRoles(query, pageRequest));
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/all")
    public Result<List<RoleVO>> all() {
        return success(roleService.listRoles());
    }

    /**
     * 根据ID获取角色详情
     *
     * @param id 角色ID
     * @return 角色详情
     */
    @SaCheckPermission("system:role:detail")
    @GetMapping("/{id}")
    public Result<Role> getInfo(@PathVariable Long id) {
        return success(roleService.getById(id));
    }

    /**
     * 新增角色
     *
     * @param dto 角色参数
     * @return 成功结果
     */
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody RoleDTO dto) {
        roleService.addRole(dto);
        return success();
    }

    /**
     * 修改角色
     *
     * @param dto 角色参数
     * @return 成功结果
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody RoleDTO dto) {
        roleService.updateRole(dto);
        return success();
    }

    /**
     * 删除角色
     *
     * @param ids 角色ID数组
     * @return 成功结果
     */
    @SaCheckPermission("system:role:delete")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable Long[] ids) {
        roleService.deleteRole(Arrays.asList(ids));
        return success();
    }

    /**
     * 获取角色菜单树
     *
     * @return 菜单树选择器数据
     */
    @SaCheckPermission("system:role:detail")
    @GetMapping("/menuTree")
    public Result<List<MenuTreeVO>> menuTree() {
        return success(menuService.listMenuTreeSelect());
    }

    /**
     * 获取角色已分配菜单ID
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @SaCheckPermission("system:role:detail")
    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> menuIds(@PathVariable Long roleId) {
        return success(menuService.listMenuIdsByRoleId(roleId));
    }
}