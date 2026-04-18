package com.fast.modules.job.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务VO
 *
 * @author fast-frame
 */
@Data
public class JobVO {

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    private String cronExpression;

    /**
     * 计划执行错误策略(1立即执行 2执行一次 3放弃)
     */
    private String misfirePolicy;

    /**
     * 是否并发执行(0允许 1禁止)
     */
    private String concurrent;

    /**
     * 状态(0正常 1暂停)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 下次执行时间（从Quartz获取）
     */
    private LocalDateTime nextValidTime;
}