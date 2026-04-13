package com.fast.modules.job.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务配置实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job")
public class Job extends BaseEntity {

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
     * 备注信息
     */
    private String remark;
}