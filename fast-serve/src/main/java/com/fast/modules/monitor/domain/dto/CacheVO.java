package com.fast.modules.monitor.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 缓存信息VO
 *
 * @author fast-frame
 */
@Data
public class CacheVO {

    /**
     * 缓存前缀列表
     */
    private List<CacheKeyVO> keys;

    /**
     * 缓存总数
     */
    private Integer total;

    /**
     * Redis 信息
     */
    private String info;
}