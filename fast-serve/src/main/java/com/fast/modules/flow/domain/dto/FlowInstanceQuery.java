package com.fast.modules.flow.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程实例查询参数
 *
 * @author fast-frame
 */
@Data
public class FlowInstanceQuery implements Serializable {

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