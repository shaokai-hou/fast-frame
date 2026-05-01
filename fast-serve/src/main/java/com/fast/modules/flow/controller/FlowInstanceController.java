package com.fast.modules.flow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.flow.domain.vo.FlowHisTaskVO;
import com.fast.modules.flow.domain.query.FlowInstanceQuery;
import com.fast.modules.flow.domain.dto.FlowInstanceStartDTO;
import com.fast.modules.flow.domain.vo.FlowInstanceVO;
import com.fast.modules.flow.service.FlowInstanceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程实例Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/flow/instance")
public class FlowInstanceController extends BaseController {

    @Resource
    private FlowInstanceService flowInstanceService;

    /**
     * 查询流程实例列表
     *
     * @param query    查询条件
     * @return 流程实例列表
     */
    @SaCheckPermission("flow:instance:list")
    @GetMapping("/list")
    public Result<List<FlowInstanceVO>> list(FlowInstanceQuery query) {
        return success(flowInstanceService.listInstances(query));
    }

    /**
     * 根据ID获取流程实例详情
     *
     * @param id 流程实例ID
     * @return 流程实例详情
     */
    @SaCheckPermission("flow:instance:detail")
    @GetMapping("/{id}")
    public Result<FlowInstanceVO> getInfo(@PathVariable Long id) {
        return success(flowInstanceService.getInstanceById(id));
    }

    /**
     * 启动流程实例
     *
     * @param dto 启动参数
     * @return 流程实例ID
     */
    @SaCheckPermission("flow:instance:start")
    @Log(title = "流程实例", businessType = BusinessType.INSERT)
    @PostMapping("/start")
    public Result<Long> start(@Validated @RequestBody FlowInstanceStartDTO dto) {
        return success(flowInstanceService.startInstance(dto));
    }

    /**
     * 终止流程实例
     *
     * @param id 流程实例ID
     * @return 结果
     */
    @SaCheckPermission("flow:instance:terminate")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> terminate(@PathVariable Long id) {
        flowInstanceService.terminateInstance(id);
        return success();
    }

    /**
     * 获取流程图
     *
     * @param id 流程实例ID
     * @return 流程图数据
     */
    @SaCheckPermission("flow:instance:detail")
    @GetMapping("/diagram/{id}")
    public Result<Object> getDiagram(@PathVariable Long id) {
        return success(flowInstanceService.getDiagram(id));
    }

    /**
     * 获取审批历史
     *
     * @param instanceId 流程实例ID
     * @return 审批历史列表
     */
    @SaCheckPermission("flow:instance:detail")
    @GetMapping("/history/{instanceId}")
    public Result<List<FlowHisTaskVO>> getApprovalHistory(@PathVariable Long instanceId) {
        return success(flowInstanceService.getApprovalHistory(instanceId));
    }
}