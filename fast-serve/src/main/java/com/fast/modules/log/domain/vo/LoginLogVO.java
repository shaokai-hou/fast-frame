package com.fast.modules.log.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志视图对象
 *
 * @author fast-frame
 */
@Data
public class LoginLogVO {

    /**
     * 访问ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 状态(0成功 1失败)
     */
    private String status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private LocalDateTime loginTime;
}