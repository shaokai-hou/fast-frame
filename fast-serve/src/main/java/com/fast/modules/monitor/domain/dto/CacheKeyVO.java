package com.fast.modules.monitor.domain.dto;

import lombok.Data;

/**
 * 缓存键名VO
 *
 * @author fast-frame
 */
@Data
public class CacheKeyVO {

    /**
     * 缓存键名
     */
    private String key;

    /**
     * 缓存前缀（用于分组）
     */
    private String prefix;

    /**
     * 缓存类型
     */
    private String type;

    /**
     * 过期时间（秒）
     */
    private Long ttl;
}