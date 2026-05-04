package com.fast.modules.monitor.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * IP黑名单DTO
 *
 * @author fast-frame
 */
@Data
public class IpBlacklistDTO {

    /**
     * 主键ID（修改时必填）
     */
    private Long id;

    /**
     * IP地址或CIDR格式
     */
    @NotBlank(message = "IP地址不能为空")
    private String ipAddress;

    /**
     * 类型(single/cidr)
     */
    @NotBlank(message = "IP类型不能为空")
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