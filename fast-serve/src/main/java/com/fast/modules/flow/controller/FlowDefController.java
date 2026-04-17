package com.fast.modules.flow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.flow.domain.dto.FlowDefQueryDTO;
import com.fast.modules.flow.domain.vo.FlowDefVO;
import com.fast.modules.flow.service.FlowDefService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程定义Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/flow/def")
public class FlowDefController extends BaseController {

    @Resource
    private FlowDefService flowDefService;

    /**
     * 查询流程定义列表
     *
     * @param dto 查询参数
     * @return 流程定义列表
     */
    @SaCheckPermission("flow:def:list")
    @GetMapping("/list")
    public Result<List<FlowDefVO>> list(FlowDefQueryDTO dto) {
        return success(flowDefService.listDefs(dto));
    }

    /**
     * 根据ID获取流程定义详情
     *
     * @param id 流程定义ID
     * @return 流程定义详情
     */
    @SaCheckPermission("flow:def:query")
    @GetMapping("/{id}")
    public Result<FlowDefVO> getInfo(@PathVariable Long id) {
        return success(flowDefService.getDefById(id));
    }

    /**
     * 发布流程定义
     *
     * @param id 流程定义ID
     * @return 结果
     */
    @SaCheckPermission("flow:def:publish")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{id}")
    public Result<Void> publish(@PathVariable Long id) {
        flowDefService.publishDef(id);
        return success();
    }

    /**
     * 取消发布流程定义
     *
     * @param id 流程定义ID
     * @return 结果
     */
    @SaCheckPermission("flow:def:unpublish")
    @Log(title = "流程定义", businessType = BusinessType.UPDATE)
    @PutMapping("/unpublish/{id}")
    public Result<Void> unpublish(@PathVariable Long id) {
        flowDefService.unpublishDef(id);
        return success();
    }

    /**
     * 删除流程定义
     *
     * @param id 流程定义ID
     * @return 结果
     */
    @SaCheckPermission("flow:def:remove")
    @Log(title = "流程定义", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        flowDefService.removeDef(id);
        return success();
    }
}