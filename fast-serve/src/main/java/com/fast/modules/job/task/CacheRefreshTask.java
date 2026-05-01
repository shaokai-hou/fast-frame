package com.fast.modules.job.task;

import com.fast.common.constant.RedisConstants;
import com.fast.framework.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 缓存刷新定时任务
 *
 * @author fast-frame
 */
@Slf4j
@Service("cacheRefreshTask")
public class CacheRefreshTask {

    /**
     * 刷新所有系统缓存
     */
    public void refreshAllCache() {
        refreshDictCache();
        refreshConfigCache();
        log.info("刷新所有系统缓存完成");
    }

    /**
     * 刷新字典缓存
     */
    public void refreshDictCache() {
        Set<String> keys = RedisHelper.scan(RedisConstants.DICT_PREFIX + "*");
        if (keys.isEmpty()) {
            log.info("无字典缓存需要刷新");
            return;
        }

        long count = RedisHelper.delete(keys);
        log.info("刷新字典缓存完成，删除 {} 个缓存Key", count);
    }

    /**
     * 刷新系统配置缓存
     */
    public void refreshConfigCache() {
        Set<String> keys = RedisHelper.scan(RedisConstants.CONFIG_PREFIX + "*");
        if (keys.isEmpty()) {
            log.info("无系统配置缓存需要刷新");
            return;
        }

        long count = RedisHelper.delete(keys);
        log.info("刷新系统配置缓存完成，删除 {} 个缓存Key", count);
    }
}