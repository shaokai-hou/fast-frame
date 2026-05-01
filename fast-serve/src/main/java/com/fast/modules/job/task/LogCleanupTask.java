package com.fast.modules.job.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fast.modules.log.domain.entity.LoginLog;
import com.fast.modules.log.domain.entity.OperLog;
import com.fast.modules.log.service.LoginLogService;
import com.fast.modules.log.service.OperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 日志清理定时任务
 *
 * @author fast-frame
 */
@Slf4j
@Service("logCleanupTask")
@RequiredArgsConstructor
public class LogCleanupTask {

    private final OperLogService operLogService;
    private final LoginLogService loginLogService;

    /**
     * 清理操作日志
     *
     * @param days 保留天数
     */
    public void cleanOperLogs(String days) {
        int retainDays = Integer.parseInt(days);
        LocalDateTime threshold = LocalDateTime.now().minusDays(retainDays);

        LambdaQueryWrapper<OperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(OperLog::getOperTime, threshold);

        long count = operLogService.count(wrapper);
        if (count > 0) {
            operLogService.remove(wrapper);
            log.info("清理操作日志完成，删除 {} 条记录，保留最近 {} 天", count, retainDays);
        } else {
            log.info("无过期操作日志需要清理，保留最近 {} 天", retainDays);
        }
    }

    /**
     * 清理登录日志
     *
     * @param days 保留天数
     */
    public void cleanLoginLogs(String days) {
        int retainDays = Integer.parseInt(days);
        LocalDateTime threshold = LocalDateTime.now().minusDays(retainDays);

        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(LoginLog::getLoginTime, threshold);

        long count = loginLogService.count(wrapper);
        if (count > 0) {
            loginLogService.remove(wrapper);
            log.info("清理登录日志完成，删除 {} 条记录，保留最近 {} 天", count, retainDays);
        } else {
            log.info("无过期登录日志需要清理，保留最近 {} 天", retainDays);
        }
    }
}