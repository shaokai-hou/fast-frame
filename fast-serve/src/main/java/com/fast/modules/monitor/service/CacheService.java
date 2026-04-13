package com.fast.modules.monitor.service;

import com.fast.common.result.PageResult;
import com.fast.modules.monitor.domain.vo.CacheInfoVO;
import com.fast.modules.monitor.domain.vo.CacheKeyVO;

import java.util.List;

/**
 * 缓存管理Service
 *
 * @author fast-frame
 */
public interface CacheService {

    /**
     * 分页查询缓存键名列表
     *
     * @param prefix   缓存前缀（可选）
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 缓存键名分页结果
     */
    PageResult<CacheKeyVO> pageCacheKeys(String prefix, Integer pageNum, Integer pageSize);

    /**
     * 获取缓存详情
     *
     * @param key 缓存键名
     * @return 缓存详情
     */
    CacheInfoVO getCacheInfo(String key);

    /**
     * 删除指定缓存
     *
     * @param key 缓存键名
     */
    void deleteCache(String key);

    /**
     * 清空业务缓存
     */
    void clearAllCache();

    /**
     * 获取Redis信息
     *
     * @return Redis信息
     */
    String getRedisInfo();
}