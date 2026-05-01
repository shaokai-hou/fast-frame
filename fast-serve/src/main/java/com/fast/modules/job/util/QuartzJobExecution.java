package com.fast.modules.job.util;

import cn.hutool.extra.spring.SpringUtil;
import com.fast.common.constant.Constants;
import com.fast.common.exception.BusinessException;
import com.fast.modules.job.domain.entity.JobLog;
import com.fast.modules.job.service.JobLogService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        JobLogService jobLogService = SpringUtil.getBean(JobLogService.class);

        // 获取任务信息
        String jobIdStr = context.getJobDetail().getJobDataMap().getString("jobId");
        String jobName = context.getJobDetail().getJobDataMap().getString("jobName");
        String jobGroup = context.getJobDetail().getJobDataMap().getString("jobGroup");
        String invokeTarget = context.getJobDetail().getJobDataMap().getString("invokeTarget");

        // 创建日志对象
        JobLog jobLog = new JobLog();
        jobLog.setJobId(Long.parseLong(jobIdStr));
        jobLog.setJobName(jobName);
        jobLog.setJobGroup(jobGroup);
        jobLog.setInvokeTarget(invokeTarget);
        jobLog.setStartTime(LocalDateTime.now());

        try {
            // 执行任务
            invokeMethod(invokeTarget);
            jobLog.setStatus(Constants.NORMAL);
            jobLog.setJobMessage("任务执行成功");
        } catch (Exception e) {
            log.error("任务执行失败，任务ID: {}", jobIdStr, e);
            jobLog.setStatus(Constants.DISABLE);
            jobLog.setJobMessage("任务执行失败");
            jobLog.setExceptionInfo(e.getMessage());
            if (context.getJobDetail().getJobDataMap().containsKey("concurrent")
                    && Constants.DISABLE.equals(context.getJobDetail().getJobDataMap().getString("concurrent"))) {
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
     * 支持格式：beanName.methodName、beanName.methodName()、beanName.methodName(params)
     *
     * @param invokeTarget 调用目标字符串
     */
    private void invokeMethod(String invokeTarget) throws Exception {
        // 解析调用目标
        int dotIndex = invokeTarget.indexOf('.');
        if (dotIndex < 0) {
            throw new BusinessException("调用目标格式错误，正确格式：beanName.methodName 或 beanName.methodName(params)");
        }

        String beanName = invokeTarget.substring(0, dotIndex);
        String methodPart = invokeTarget.substring(dotIndex + 1);

        // 解析方法名和参数
        String methodName;
        String params = null;

        int parenIndex = methodPart.indexOf('(');
        if (parenIndex > 0) {
            // 有括号：methodName(params) 或 methodName()
            methodName = methodPart.substring(0, parenIndex);
            int closeParenIndex = methodPart.indexOf(')');
            if (closeParenIndex > parenIndex + 1) {
                // 有参数
                params = methodPart.substring(parenIndex + 1, closeParenIndex);
            }
        } else {
            // 无括号：methodName
            methodName = methodPart;
        }

        // 获取Bean
        Object bean = SpringUtil.getBean(beanName);

        // 反射调用方法
        if (params != null) {
            bean.getClass().getMethod(methodName, String.class).invoke(bean, params);
        } else {
            bean.getClass().getMethod(methodName).invoke(bean);
        }
    }
}