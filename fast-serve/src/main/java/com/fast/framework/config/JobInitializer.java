package com.fast.framework.config;

import com.fast.modules.job.domain.entity.Job;
import com.fast.modules.job.mapper.JobMapper;
import com.fast.modules.job.util.ScheduleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务初始化器
 * 应用启动时从数据库加载任务到 Quartz 调度器
 *
 * @author fast-frame
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobInitializer {

    private final Scheduler scheduler;
    private final JobMapper jobMapper;

    /**
     * 应用启动完成后初始化任务
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initJobs() {
        try {
            // 查询所有未删除的任务
            List<Job> jobs = jobMapper.selectList(null);
            if (jobs.isEmpty()) {
                log.info("无定时任务需要初始化");
                return;
            }

            int successCount = 0;
            int failCount = 0;

            for (Job job : jobs) {
                try {
                    ScheduleUtils.createJob(scheduler, job);
                    successCount++;
                } catch (Exception e) {
                    log.warn("初始化任务失败: {}, 错误: {}", job.getJobName(), e.getMessage());
                    failCount++;
                }
            }

            log.info("定时任务初始化完成，成功 {} 个，失败 {} 个", successCount, failCount);
        } catch (Exception e) {
            log.error("定时任务初始化异常: {}", e.getMessage());
        }
    }
}