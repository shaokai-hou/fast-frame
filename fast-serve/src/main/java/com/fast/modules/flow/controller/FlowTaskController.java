package com.fast.modules.flow.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fast.common.enums.BusinessType;
import com.fast.common.result.Result;
import com.fast.framework.annotation.Log;
import com.fast.framework.web.BaseController;
import com.fast.modules.flow.domain.dto.FlowTaskApproveDTO;
import com.fast.modules.flow.domain.query.FlowTaskQuery;
import com.fast.modules.flow.domain.vo.FlowTaskVO;
import com.fast.modules.flow.service.FlowTaskService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 流程任务Controller
 *
 * @author fast-frame
 */
@RestController
@RequestMapping("/flow/task")
public class FlowTaskController extends BaseController {

    @Resource
    private FlowTaskService flowTaskService;

    /**
     * 查询待办任务列表
     *
     * @param query    查询条件
     * @return 待办任务列表
     */
    @SaCheckPermission("flow:task:todo")
    @GetMapping("/todo")
    public Result<List<FlowTaskVO>> todoList(FlowTaskQuery query) {
        return success(flowTaskService.listTodoTasks(query));
    }

    /**
     * 查询已办任务列表
     *
     * @param query    查询条件
     * @return 已办任务列表
     */
    @SaCheckPermission("flow:task:done")
    @GetMapping("/done")
    public Result<List<FlowTaskVO>> doneList(FlowTaskQuery query) {
        return success(flowTaskService.listDoneTasks(query));
    }

    /**
     * 根据ID获取任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    @SaCheckPermission("flow:task:detail")
    @GetMapping("/{id}")
    public Result<FlowTaskVO> getInfo(@PathVariable Long id) {
        return success(flowTaskService.getTaskById(id));
    }

    /**
     * 审批通过
     *
     * @param dto 审批参数
     * @return 结果
     */
    @SaCheckPermission("flow:task:approve")
    @Log(title = "任务审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public Result<Void> approve(@Validated @RequestBody FlowTaskApproveDTO dto) {
        flowTaskService.approveTask(dto);
        return success();
    }

    /**
     * 审批驳回
     *
     * @param dto 审批参数
     * @return 结果
     */
    @SaCheckPermission("flow:task:reject")
    @Log(title = "任务驳回", businessType = BusinessType.UPDATE)
    @PostMapping("/reject")
    public Result<Void> reject(@Validated @RequestBody FlowTaskApproveDTO dto) {
        flowTaskService.rejectTask(dto);
        return success();
    }

    /**
     * 退回上一节点
     *
     * @param dto 审批参数
     * @return 结果
     */
    @SaCheckPermission("flow:task:back")
    @Log(title = "任务退回", businessType = BusinessType.UPDATE)
    @PostMapping("/back")
    public Result<Void> back(@Validated @RequestBody FlowTaskApproveDTO dto) {
        flowTaskService.backTask(dto);
        return success();
    }

    /**
     * 委派任务
     *
     * @param dto 审批参数
     * @return 结果
     */
    @SaCheckPermission("flow:task:delegate")
    @Log(title = "任务委派", businessType = BusinessType.UPDATE)
    @PostMapping("/delegate")
    public Result<Void> delegate(@Validated @RequestBody FlowTaskApproveDTO dto) {
        flowTaskService.delegateTask(dto);
        return success();
    }
}