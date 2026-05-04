package com.fast.modules.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * IP黑名单实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_ip_blacklist")
public class IpBlacklist extends BaseEntity {

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
     * 过期时间(空表示永久)
     */
    private LocalDateTime expireTime;

    /**
     * 状态(0启用 1禁用)
     */
    private String status;
}