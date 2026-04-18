package com.fast.modules.monitor.domain.dto;

import lombok.Data;

/**
 * 缓存详情VO
 *
 * @author fast-frame
 */
@Data
public class CacheInfoVO {

    /**
     * 缓存键名
     */
    private String key;

    /**
     * 缓存值
     */
    private Object value;

    /**
     * 缓存类型（string/hash/list/set/zset）
     */
    private String type;

    /**
     * 过期时间（秒）
     */
    private Long ttl;

    /**
     * 缓存大小（字节）
     */
    private Long size;
}