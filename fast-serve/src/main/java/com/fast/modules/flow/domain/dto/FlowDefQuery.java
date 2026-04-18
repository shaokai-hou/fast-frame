package com.fast.modules.flow.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程定义查询参数
 *
 * @author fast-frame
 */
@Data
public class FlowDefQuery implements Serializable {

    private static final long serialVersionUID = 1L;

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