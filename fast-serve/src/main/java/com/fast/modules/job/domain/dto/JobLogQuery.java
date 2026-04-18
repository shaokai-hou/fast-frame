package com.fast.modules.job.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务日志查询参数
 *
 * @author fast-frame
 */
@Data
public class JobLogQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 执行状态
     */
    private String status;
}