package com.fast.modules.monitor.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存前缀VO
 *
 * @author fast-frame
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CachePrefixVO {

    /**
     * 常量名称
     */
    private String name;

    /**
     * 前缀值（去掉末尾冒号）
     */
    private String value;
}
