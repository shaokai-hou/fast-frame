package com.fast.modules.flow.service;

import com.fast.modules.flow.domain.dto.FlowTaskApproveDTO;
import com.fast.modules.flow.domain.dto.FlowTaskQueryDTO;
import com.fast.modules.flow.domain.vo.FlowTaskVO;

import java.util.List;

/**
 * 流程任务Service接口
 *
 * @author fast-frame
 */
public interface FlowTaskService {

    /**
     * 查询待办任务列表
     *
     * @param dto 查询参数
     * @return 待办任务列表
     */
    List<FlowTaskVO> listTodoTasks(FlowTaskQueryDTO dto);

    /**
     * 查询已办任务列表
     *
     * @param dto 查询参数
     * @return 已办任务列表
     */
    List<FlowTaskVO> listDoneTasks(FlowTaskQueryDTO dto);

    /**
     * 根据ID获取任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    FlowTaskVO getTaskById(Long id);

    /**
     * 审批通过
     *
     * @param dto 审批参数
     */
    void approveTask(FlowTaskApproveDTO dto);

    /**
     * 审批驳回
     *
     * @param dto 审批参数
     */
    void rejectTask(FlowTaskApproveDTO dto);

    /**
     * 退回上一节点
     *
     * @param dto 审批参数
     */
    void backTask(FlowTaskApproveDTO dto);

    /**
     * 委派任务
     *
     * @param dto 审批参数
     */
    void delegateTask(FlowTaskApproveDTO dto);
}