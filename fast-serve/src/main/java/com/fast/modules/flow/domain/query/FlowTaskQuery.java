package com.fast.modules.flow.domain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程任务查询参数
 *
 * @author fast-frame
 */
@Data
public class FlowTaskQuery implements Serializable {

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