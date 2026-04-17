package com.fast.modules.flow.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程任务VO
 *
 * @author fast-frame
 */
@Data
public class FlowTaskVO {

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 节点编码
     */
    private String nodeCode;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 流程状态
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
     * 审批时间
     */
    private Date approveTime;

    /**
     * 业务ID (关联实例的业务ID)
     */
    private String businessId;

    /**
     * 流程定义名称
     */
    private String flowName;

    /**
     * 审批意见
     */
    private String message;
}