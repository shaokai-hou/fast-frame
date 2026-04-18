package com.fast.modules.monitor.domain.dto;

import lombok.Data;

/**
 * 系统信息VO
 *
 * @author fast-frame
 */
@Data
public class SysVO {

    /**
     * 服务器名称
     */
    private String hostName;

    /**
     * 服务器IP
     */
    private String ip;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 操作系统架构
     */
    private String osArch;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * 用户名
     */
    private String userName;
}