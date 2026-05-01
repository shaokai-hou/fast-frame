package com.fast.modules.monitor.domain.vo;

import lombok.Data;

/**
 * 磁盘信息VO
 *
 * @author fast-frame
 */
@Data
public class SysFileVO {

    /**
     * 磁盘名称
     */
    private String dirName;

    /**
     * 磁盘类型
     */
    private String sysTypeName;

    /**
     * 磁盘总大小（GB）
     */
    private Double total;

    /**
     * 已用大小（GB）
     */
    private Double used;

    /**
     * 剩余大小（GB）
     */
    private Double free;

    /**
     * 使用率
     */
    private Double usage;
}