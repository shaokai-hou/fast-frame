package com.fast.modules.flow.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 流程任务查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowTaskQuery extends PageRequest {

    private static final long serialVersionUID = 1L;

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