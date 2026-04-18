package com.fast.modules.monitor.domain.dto;

import lombok.Data;

/**
 * 内存信息VO
 *
 * @author fast-frame
 */
@Data
public class MemVO {

    /**
     * 内存总量（GB）
     */
    private Double total;

    /**
     * 已用内存（GB）
     */
    private Double used;

    /**
     * 剩余内存（GB）
     */
    private Double free;

    /**
     * 使用率
     */
    private Double usage;
}