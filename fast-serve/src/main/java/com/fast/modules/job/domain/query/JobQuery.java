package com.fast.modules.job.domain.query;

import lombok.Data;

/**
 * 定时任务查询DTO
 *
 * @author fast-frame
 */
@Data
public class JobQuery {

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 状态(0正常 1暂停)
     */
    private String status;

    /**
     * 调用目标字符串
     */
    private String invokeTarget;
}