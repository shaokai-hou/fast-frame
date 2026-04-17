package com.fast.modules.flow.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 流程定义VO
 *
 * @author fast-frame
 */
@Data
public class FlowDefVO {

    /**
     * 流程定义ID
     */
    private Long id;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 流程名称
     */
    private String flowName;

    /**
     * 流程类别
     */
    private String category;

    /**
     * 版本号
     */
    private String version;

    /**
     * 发布状态 (0未发布 1已发布 9失效)
     */
    private Integer isPublish;

    /**
     * 激活状态 (0挂起 1激活)
     */
    private Integer activityStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}