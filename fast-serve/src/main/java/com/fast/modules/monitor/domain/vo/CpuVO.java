package com.fast.modules.monitor.domain.vo;

import lombok.Data;

/**
 * CPU信息VO
 *
 * @author fast-frame
 */
@Data
public class CpuVO {

    /**
     * 核心数
     */
    private Integer core;

    /**
     * CPU总使用率
     */
    private Double used;

    /**
     * CPU空闲率
     */
    private Double idle;
}