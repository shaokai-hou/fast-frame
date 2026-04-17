package com.fast.modules.job.util;

import com.fast.modules.job.domain.entity.Job;
import org.quartz.*;

/**
 * 定时任务调度工具类
 *
 * @author fast-frame
 */
public class ScheduleUtils {

    /**
     * 获取Quartz任务Key
     *
     * @param jobId 任务ID
     * @param jobGroup 任务分组
     * @return JobKey
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(String.valueOf(jobId), jobGroup);
    }

    /**
     * 获取Quartz触发器Key
     *
     * @param jobId 任务ID
     * @param jobGroup 任务分组
     * @return TriggerKey
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(String.valueOf(jobId), jobGroup);
    }

    /**
     * 创建定时任务
     *
     * @param scheduler 调度器
     * @param job 任务配置
     * @throws SchedulerException 调度异常
     */
    public static void createJob(Scheduler scheduler, Job job) throws SchedulerException {
        // 构建Job信息
        JobDetail jobDetail = JobBuilder.newJob(QuartzJobExecution.class)
                .withIdentity(getJobKey(job.getId(), job.getJobGroup()))
                .usingJobData("jobId", String.valueOf(job.getId()))
                .usingJobData("jobGroup", job.getJobGroup())
                .usingJobData("invokeTarget", job.getInvokeTarget())
                .build();

        // 构建触发器
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(job.getId(), job.getJobGroup()))
                .withSchedule(createCronScheduleBuilder(job))
                .build();

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(job.getId(), job.getJobGroup()))) {
            // 防止创建时存在数据问题，先删除再创建
            scheduler.deleteJob(getJobKey(job.getId(), job.getJobGroup()));
        }

        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停任务
        if ("1".equals(job.getStatus())) {
            scheduler.pauseJob(getJobKey(job.getId(), job.getJobGroup()));
        }
    }

    /**
     * 更新定时任务
     *
     * @param scheduler 调度器
     * @param job 任务配置
     * @throws SchedulerException 调度异常
     */
    public static void updateJob(Scheduler scheduler, Job job) throws SchedulerException {
        TriggerKey triggerKey = getTriggerKey(job.getId(), job.getJobGroup());

        // 获取触发器
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        if (trigger == null) {
            // 触发器不存在，创建新任务
            createJob(scheduler, job);
            return;
        }

        // 更新触发器
        trigger = trigger.getTriggerBuilder()
                .withSchedule(createCronScheduleBuilder(job))
                .build();

        // 更新调度
        scheduler.rescheduleJob(triggerKey, trigger);

        // 暂停任务
        if ("1".equals(job.getStatus())) {
            scheduler.pauseJob(getJobKey(job.getId(), job.getJobGroup()));
        } else {
            scheduler.resumeJob(getJobKey(job.getId(), job.getJobGroup()));
        }
    }

    /**
     * 删除定时任务
     *
     * @param scheduler 调度器
     * @param jobId 任务ID
     * @param jobGroup 任务分组
     * @throws SchedulerException 调度异常
     */
    public static void deleteJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.deleteJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 暂停定时任务
     *
     * @param scheduler 调度器
     * @param jobId 任务ID
     * @param jobGroup 任务分组
     * @throws SchedulerException 调度异常
     */
    public static void pauseJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 恢复定时任务
     *
     * @param scheduler 调度器
     * @param jobId 任务ID
     * @param jobGroup 任务分组
     * @throws SchedulerException 调度异常
     */
    public static void resumeJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 立即执行一次任务
     *
     * @param scheduler 调度器
     * @param jobId 任务ID
     * @param jobGroup 任务分组
     * @throws SchedulerException 调度异常
     */
    public static void runJob(Scheduler scheduler, Long jobId, String jobGroup) throws SchedulerException {
        scheduler.triggerJob(getJobKey(jobId, jobGroup));
    }

    /**
     * 创建Cron调度构建器
     *
     * @param job 任务配置
     * @return CronScheduleBuilder
     */
    private static CronScheduleBuilder createCronScheduleBuilder(Job job) {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

        // 错过执行策略
        String misfirePolicy = job.getMisfirePolicy();
        if ("1".equals(misfirePolicy)) {
            // 立即执行
            scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
        } else if ("2".equals(misfirePolicy)) {
            // 执行一次
            scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
        } else {
            // 放弃（默认）
            scheduleBuilder.withMisfireHandlingInstructionDoNothing();
        }

        return scheduleBuilder;
    }
}