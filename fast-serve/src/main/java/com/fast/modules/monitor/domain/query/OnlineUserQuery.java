package com.fast.modules.monitor.domain.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 在线用户查询参数
 *
 * @author fast-frame
 */
@Data
public class OnlineUserQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;
}