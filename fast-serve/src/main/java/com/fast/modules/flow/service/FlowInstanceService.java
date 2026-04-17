package com.fast.modules.flow.service;

import com.fast.modules.flow.domain.dto.FlowInstanceQueryDTO;
import com.fast.modules.flow.domain.dto.FlowInstanceStartDTO;
import com.fast.modules.flow.domain.vo.FlowHisTaskVO;
import com.fast.modules.flow.domain.vo.FlowInstanceVO;

import java.util.List;

/**
 * 流程实例Service接口
 *
 * @author fast-frame
 */
public interface FlowInstanceService {

    /**
     * 查询流程实例列表
     *
     * @param dto 查询参数
     * @return 流程实例列表
     */
    List<FlowInstanceVO> listInstances(FlowInstanceQueryDTO dto);

    /**
     * 根据ID获取流程实例详情
     *
     * @param id 流程实例ID
     * @return 流程实例详情
     */
    FlowInstanceVO getInstanceById(Long id);

    /**
     * 启动流程实例
     *
     * @param dto 启动参数
     * @return 流程实例ID
     */
    Long startInstance(FlowInstanceStartDTO dto);

    /**
     * 终止流程实例
     *
     * @param id 流程实例ID
     */
    void terminateInstance(Long id);

    /**
     * 获取流程图
     *
     * @param id 流程实例ID
     * @return 流程图数据
     */
    Object getDiagram(Long id);

    /**
     * 获取审批历史
     *
     * @param instanceId 流程实例ID
     * @return 审批历史列表(按时间正序)
     */
    List<FlowHisTaskVO> getApprovalHistory(Long instanceId);
}