package com.fast.modules.job.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务日志VO
 *
 * @author fast-frame
 */
@Data
public class JobLogVO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long jobId;

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
     * 日志信息
     */
    private String jobMessage;

    /**
     * 执行状态(0成功 1失败)
     */
    private String status;

    /**
     * 异常信息
     */
    private String exceptionInfo;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时（毫秒）
     */
    private Long duration;

    /**
     * 计算执行耗时
     *
     * @return 执行耗时（毫秒）
     */
    public Long getDuration() {
        if (startTime != null && endTime != null) {
            return endTime.minusNanos(startTime.toEpochSecond(java.time.ZoneOffset.UTC) * 1000000000 + startTime.getNano())
                    .toEpochSecond(java.time.ZoneOffset.UTC) * 1000;
        }
        return null;
    }
}