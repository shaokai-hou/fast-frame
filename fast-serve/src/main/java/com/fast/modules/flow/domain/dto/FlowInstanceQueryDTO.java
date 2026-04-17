package com.fast.modules.flow.domain.dto;

import lombok.Data;

/**
 * 流程实例查询DTO
 *
 * @author fast-frame
 */
@Data
public class FlowInstanceQueryDTO {

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 流程状态
     */
    private String flowStatus;
}