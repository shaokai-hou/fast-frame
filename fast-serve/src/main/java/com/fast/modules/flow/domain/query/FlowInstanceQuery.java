package com.fast.modules.flow.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 流程实例查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowInstanceQuery extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 流程状态
     */
    private String flowStatus;
}