package com.fast.modules.system.controller;

import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.annotation.RequiresPermission;
import com.fast.common.enums.BusinessType;
import com.fast.framework.web.BaseController;
import com.fast.modules.system.dto.DeptDTO;
import com.fast.modules.system.entity.Dept;
import com.fast.modules.system.service.DeptService;
import com.fast.modules.system.vo.DeptTreeVO;
import com.fast.modules.system.vo.DeptVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/system/dept")
public class DeptController extends BaseController {

    @Resource
    private DeptService deptService;

    /**
     * 查询部门树
     *
     * @param dto 查询参数
     * @return 部门树列表
     */
    @RequiresPermission("system:dept:list")
    @GetMapping("/list")
    public Result<List<DeptVO>> list(DeptDTO dto) {
        return success(deptService.listDeptTree(dto));
    }

    /**
     * 查询部门树选择器
     *
     * @return 部门树选择器数据
     */
    @RequiresPermission("system:dept:list")
    @GetMapping("/tree")
    public Result<List<DeptTreeVO>> tree() {
        return success(deptService.listDeptTreeSelect());
    }

    /**
     * 根据ID获取部门详情
     *
     * @param id 部门ID
     * @return 部门详情
     */
    @RequiresPermission("system:dept:query")
    @GetMapping("/{id}")
    public Result<Dept> getInfo(@PathVariable Long id) {
        return success(deptService.getById(id));
    }

    /**
     * 根据角色ID查询部门ID列表
     *
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    @RequiresPermission("system:dept:list")
    @GetMapping("/roleDeptIds/{roleId}")
    public Result<List<Long>> getRoleDeptIds(@PathVariable Long roleId) {
        return success(deptService.getDeptIdsByRoleId(roleId));
    }

    /**
     * 新增部门
     *
     * @param dept 部门信息
     * @return 成功结果
     */
    @RequiresPermission("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody Dept dept) {
        deptService.addDept(dept);
        return success();
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息
     * @return 成功结果
     */
    @RequiresPermission("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody Dept dept) {
        deptService.updateDept(dept);
        return success();
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 成功结果
     */
    @RequiresPermission("system:dept:delete")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        deptService.deleteDept(id);
        return success();
    }
}