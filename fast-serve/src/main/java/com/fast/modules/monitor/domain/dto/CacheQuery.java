package com.fast.modules.monitor.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 缓存查询参数
 *
 * @author fast-frame
 */
@Data
public class CacheQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 缓存前缀
     */
    private String prefix;
}