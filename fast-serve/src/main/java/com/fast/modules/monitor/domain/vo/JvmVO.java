package com.fast.modules.monitor.domain.vo;

import lombok.Data;

/**
 * JVM信息VO
 *
 * @author fast-frame
 */
@Data
public class JvmVO {

    /**
     * JVM总内存（MB）
     */
    private Double total;

    /**
     * JVM已用内存（MB）
     */
    private Double used;

    /**
     * JVM空闲内存（MB）
     */
    private Double free;

    /**
     * JVM最大内存（MB）
     */
    private Double max;

    /**
     * 使用率
     */
    private Double usage;

    /**
     * Java版本
     */
    private String version;

    /**
     * Java Home
     */
    private String home;

    /**
     * JVM名称
     */
    private String name;

    /**
     * JVM启动时间
     */
    private String startTime;

    /**
     * JVM运行时长（毫秒）
     */
    private Long runTime;
}