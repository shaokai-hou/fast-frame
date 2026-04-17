package com.fast.modules.flow.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 任务审批DTO
 *
 * @author fast-frame
 */
@Data
public class FlowTaskApproveDTO {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 流程变量 (JSON格式)
     */
    private String variable;
}