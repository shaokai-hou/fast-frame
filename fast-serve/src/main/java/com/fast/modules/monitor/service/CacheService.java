package com.fast.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fast.modules.monitor.domain.query.CacheQuery;
import com.fast.modules.monitor.domain.vo.CacheInfoVO;
import com.fast.modules.monitor.domain.vo.CacheKeyVO;
import com.fast.modules.monitor.domain.vo.CachePrefixVO;

import java.util.List;

/**
 * 缓存管理Service
 *
 * @author fast-frame
 */
public interface CacheService {

    /**
     * 获取所有缓存前缀列表
     *
     * @return 缓存前缀列表
     */
    List<CachePrefixVO> getCachePrefixes();

    /**
     * 分页查询缓存键名列表
     *
     * @param query 查询条件
     * @return 缓存键名分页结果
     */
    IPage<CacheKeyVO> pageCacheKeys(CacheQuery query);

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