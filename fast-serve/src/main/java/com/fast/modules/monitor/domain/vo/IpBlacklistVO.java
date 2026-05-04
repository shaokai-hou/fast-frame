package com.fast.modules.monitor.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * IP黑名单视图对象
 *
 * @author fast-frame
 */
@Data
public class IpBlacklistVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * IP地址或CIDR格式
     */
    private String ipAddress;

    /**
     * 类型(single/cidr)
     */
    private String ipType;

    /**
     * 封禁原因
     */
    private String reason;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 状态(0启用 1禁用)
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人名称
     */
    private String createByName;
}