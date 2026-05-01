package com.fast.modules.job.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fast.framework.helper.MinioHelper;
import com.fast.modules.system.domain.entity.File;
import com.fast.modules.system.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件清理定时任务
 *
 * @author fast-frame
 */
@Slf4j
@Service("fileCleanupTask")
@RequiredArgsConstructor
public class FileCleanupTask {

    private final FileMapper fileMapper;

    /**
     * 清理过期临时文件
     *
     * @param days 保留天数
     */
    @Transactional(rollbackFor = Exception.class)
    public void cleanTempFiles(String days) {
        int retainDays = Integer.parseInt(days);
        LocalDateTime threshold = LocalDateTime.now().minusDays(retainDays);

        // 查询过期文件（逻辑删除的文件不算）
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(File::getCreateTime, threshold);
        wrapper.eq(File::getDelFlag, "0");

        List<File> expiredFiles = fileMapper.selectList(wrapper);

        if (expiredFiles.isEmpty()) {
            log.info("无过期文件需要清理，保留最近 {} 天", retainDays);
            return;
        }

        int successCount = 0;
        int failCount = 0;

        for (File file : expiredFiles) {
            try {
                // 先删除 MinIO 文件
                MinioHelper.delete(file.getBucketType(), file.getFileName());
                // 再删除数据库记录（物理删除）
                fileMapper.deleteById(file.getId());
                successCount++;
            } catch (Exception e) {
                log.warn("删除文件失败: {}, 错误: {}", file.getFileName(), e.getMessage());
                failCount++;
            }
        }

        log.info("清理过期文件完成，成功删除 {} 个，失败 {} 个，保留最近 {} 天",
                successCount, failCount, retainDays);
    }
}