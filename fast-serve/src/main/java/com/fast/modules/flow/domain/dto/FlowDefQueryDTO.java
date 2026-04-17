package com.fast.modules.flow.domain.dto;

import lombok.Data;

/**
 * 流程定义查询DTO
 *
 * @author fast-frame
 */
@Data
public class FlowDefQueryDTO {

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 发布状态 (0未发布 1已发布 9失效)
     */
    private Integer isPublish;

    /**
     * 激活状态 (0挂起 1激活)
     */
    private Integer activityStatus;
}