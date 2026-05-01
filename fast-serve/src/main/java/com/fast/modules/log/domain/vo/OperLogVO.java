package com.fast.modules.log.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象
 *
 * @author fast-frame
 */
@Data
public class OperLogVO {

    /**
     * 日志主键
     */
    private Long id;

    /**
     * 模块标题
     */
    private String title;

    /**
     * 操作类型
     */
    private Integer businessType;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 操作人员
     */
    private String operName;

    /**
     * 请求URL
     */
    private String operUrl;

    /**
     * 操作IP
     */
    private String operIp;

    /**
     * 操作地点
     */
    private String operLocation;

    /**
     * 状态(0正常 1异常)
     */
    private String status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;

    /**
     * 追踪ID
     */
    private String traceId;
}