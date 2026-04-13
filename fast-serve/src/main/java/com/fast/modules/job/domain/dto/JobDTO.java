package com.fast.modules.job.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 定时任务DTO
 *
 * @author fast-frame
 */
@Data
public class JobDTO {

    /**
     * 任务ID
     */
    private Long id;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 64, message = "任务名称长度不能超过64个字符")
    private String jobName;

    /**
     * 任务分组
     */
    @NotBlank(message = "任务分组不能为空")
    @Size(max = 64, message = "任务分组长度不能超过64个字符")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @NotBlank(message = "调用目标字符串不能为空")
    @Size(max = 500, message = "调用目标字符串长度不能超过500个字符")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @NotBlank(message = "cron执行表达式不能为空")
    @Size(max = 255, message = "cron执行表达式长度不能超过255个字符")
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
     * 备注信息
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}