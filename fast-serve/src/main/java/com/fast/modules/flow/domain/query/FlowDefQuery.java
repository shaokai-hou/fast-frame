package com.fast.modules.flow.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 流程定义查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowDefQuery extends PageRequest {

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