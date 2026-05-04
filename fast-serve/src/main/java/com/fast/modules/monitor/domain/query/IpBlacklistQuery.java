package com.fast.modules.monitor.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IP黑名单查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IpBlacklistQuery extends PageRequest {

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 类型(single/cidr)
     */
    private String ipType;

    /**
     * 状态(0启用 1禁用)
     */
    private String status;
}