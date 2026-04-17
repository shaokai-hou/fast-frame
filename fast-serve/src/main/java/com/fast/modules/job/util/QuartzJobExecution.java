package com.fast.modules.job.util;

import com.fast.common.util.SpringContextHolder;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.service.JobLogService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

/**
 * Quartz定时任务执行类
 *
 * @author fast-frame
 */
public class QuartzJobExecution extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(QuartzJobExecution.class);

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobLogService jobLogService = SpringContextHolder.getBean(JobLogService.class);

        // 获取任务信息
        String jobIdStr = context.getJobDetail().getJobDataMap().getString("jobId");
        String jobGroup = context.getJobDetail().getJobDataMap().getString("jobGroup");
        String invokeTarget = context.getJobDetail().getJobDataMap().getString("invokeTarget");

        // 创建日志对象
        JobLog jobLog = new JobLog();
        jobLog.setJobId(Long.parseLong(jobIdStr));
        jobLog.setJobName(context.getJobDetail().getKey().getName());
        jobLog.setJobGroup(jobGroup);
        jobLog.setInvokeTarget(invokeTarget);
        jobLog.setStartTime(LocalDateTime.now());

        try {
            // 执行任务
            invokeMethod(invokeTarget);
            jobLog.setStatus("0");
            jobLog.setJobMessage("任务执行成功");
        } catch (Exception e) {
            log.error("任务执行失败，任务ID: {}", jobIdStr, e);
            jobLog.setStatus("1");
            jobLog.setJobMessage("任务执行失败");
            jobLog.setExceptionInfo(e.getMessage());
            if (context.getJobDetail().getJobDataMap().containsKey("concurrent")
                    && "1".equals(context.getJobDetail().getJobDataMap().getString("concurrent"))) {
                // 禁止并发，重新触发
                throw new JobExecutionException(e);
            }
        } finally {
            jobLog.setEndTime(LocalDateTime.now());
            // 保存日志
            jobLogService.save(jobLog);
        }
    }

    /**
     * 执行调用方法
     *
     * @param invokeTarget 调用目标字符串
     */
    private void invokeMethod(String invokeTarget) throws Exception {
        // 解析调用目标：beanName.methodName 或 beanName.methodName(params)
        String[] split = invokeTarget.split("\\.");
        if (split.length < 2) {
            throw new RuntimeException("调用目标格式错误，正确格式：beanName.methodName 或 beanName.methodName(params)");
        }

        String beanName = split[0];
        String methodName = split[1];
        String params = split.length > 2 ? split[2] : null;

        // 获取Bean
        Object bean = SpringContextHolder.getBean(beanName);

        // 反射调用方法
        if (params != null) {
            bean.getClass().getMethod(methodName, String.class).invoke(bean, params);
        } else {
            bean.getClass().getMethod(methodName).invoke(bean);
        }
    }
}