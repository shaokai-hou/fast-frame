package com.fast.modules.monitor.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 缓存查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CacheQuery extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 缓存前缀
     */
    private String prefix;
}