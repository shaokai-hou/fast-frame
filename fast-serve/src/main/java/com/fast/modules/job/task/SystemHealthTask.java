package com.fast.modules.job.task;

import com.fast.modules.monitor.domain.dto.ServerVO;
import com.fast.modules.monitor.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统健康检查定时任务
 *
 * @author fast-frame
 */
@Slf4j
@Service("systemHealthTask")
@RequiredArgsConstructor
public class SystemHealthTask {

    private final ServerService serverService;

    // 告警阈值
    private static final double CPU_WARN_THRESHOLD = 80.0;
    private static final double MEM_WARN_THRESHOLD = 85.0;
    private static final double DISK_WARN_THRESHOLD = 90.0;

    /**
     * 检查系统健康状态
     */
    public void checkSystemHealth() {
        ServerVO server = serverService.getServerInfo();

        // CPU 使用率检查
        double cpuUsage = server.getCpu().getUsed();
        if (cpuUsage > CPU_WARN_THRESHOLD) {
            log.warn("CPU使用率告警: {}%，超过阈值 {}%", cpuUsage, CPU_WARN_THRESHOLD);
        }

        // 内存使用率检查
        double memUsage = server.getMem().getUsage();
        if (memUsage > MEM_WARN_THRESHOLD) {
            log.warn("内存使用率告警: {}%，超过阈值 {}%", memUsage, MEM_WARN_THRESHOLD);
        }

        // JVM 内存使用率检查
        double jvmUsage = server.getJvm().getUsage();
        if (jvmUsage > MEM_WARN_THRESHOLD) {
            log.warn("JVM内存使用率告警: {}%，超过阈值 {}%", jvmUsage, MEM_WARN_THRESHOLD);
        }

        // 磁盘使用率检查
        server.getSysFiles().forEach(disk -> {
            double diskUsage = disk.getUsage();
            if (diskUsage > DISK_WARN_THRESHOLD) {
                log.warn("磁盘[{}]使用率告警: {}%，超过阈值 {}%",
                        disk.getDirName(), diskUsage, DISK_WARN_THRESHOLD);
            }
        });

        log.info("系统健康检查完成 - CPU: {}%, 内存: {}%, JVM: {}%",
                cpuUsage, memUsage, jvmUsage);
    }
}