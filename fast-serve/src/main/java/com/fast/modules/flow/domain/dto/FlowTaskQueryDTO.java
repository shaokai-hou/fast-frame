package com.fast.modules.flow.domain.dto;

import lombok.Data;

/**
 * 任务查询DTO
 *
 * @author fast-frame
 */
@Data
public class FlowTaskQueryDTO {

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程状态（已办任务使用）
     */
    private String flowStatus;
}