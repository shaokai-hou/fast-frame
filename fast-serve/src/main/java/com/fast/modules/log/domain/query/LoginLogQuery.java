package com.fast.modules.log.domain.query;

import lombok.Data;

/**
 * 登录日志查询参数
 *
 * @author fast-frame
 */
@Data
public class LoginLogQuery {

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态(0成功 1失败)
     */
    private String status;
}