package com.fast.modules.monitor.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 在线用户查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OnlineUserQuery extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;
}