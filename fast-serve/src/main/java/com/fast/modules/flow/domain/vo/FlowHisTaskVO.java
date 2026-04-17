package com.fast.modules.flow.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 审批历史VO
 *
 * @author fast-frame
 */
@Data
public class FlowHisTaskVO {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 开始节点编码
     */
    private String nodeCode;

    /**
     * 开始节点名称
     */
    private String nodeName;

    /**
     * 目标节点编码
     */
    private String targetNodeCode;

    /**
     * 目标节点名称
     */
    private String targetNodeName;

    /**
     * 审批者
     */
    private String approver;

    /**
     * 流转类型(PASS通过 REJECT驳回 NONE无动作)
     */
    private String skipType;

    /**
     * 流转类型文本(通过/驳回)
     */
    private String skipTypeText;

    /**
     * 审批意见
     */
    private String message;

    /**
     * 任务开始时间
     */
    private Date createTime;

    /**
     * 审批完成时间
     */
    private Date updateTime;
}