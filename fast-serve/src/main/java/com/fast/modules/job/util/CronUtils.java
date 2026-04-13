package com.fast.modules.job.util;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * Cron表达式工具类
 *
 * @author fast-frame
 */
public class CronUtils {

    /**
     * 校验Cron表达式是否有效
     *
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    public static boolean isValid(String cronExpression) {
        try {
            new CronExpression(cronExpression);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 获取下次执行时间
     *
     * @param cronExpression Cron表达式
     * @return 下次执行时间
     */
    public static Date getNextExecution(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取Cron表达式描述
     *
     * @param cronExpression Cron表达式
     * @return 描述
     */
    public static String getDescription(String cronExpression) {
        if (!isValid(cronExpression)) {
            return "无效的表达式";
        }
        // 简单描述，可根据需要扩展
        return cronExpression;
    }
}