package com.fast.modules.flow.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程实例VO
 *
 * @author fast-frame
 */
@Data
public class FlowInstanceVO {

    /**
     * 流程实例ID
     */
    private Long id;

    /**
     * 流程定义ID
     */
    private Long definitionId;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 当前节点编码
     */
    private String nodeCode;

    /**
     * 当前节点名称
     */
    private String nodeName;

    /**
     * 流程状态 (0:待提交 1:审批中 2:审批通过 3:自动完成 4:终止 8:已完成 9:已退回)
     */
    private String flowStatus;

    /**
     * 流程状态文本
     */
    private String flowStatusText;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 结束时间
     */
    private Date endTime;
}