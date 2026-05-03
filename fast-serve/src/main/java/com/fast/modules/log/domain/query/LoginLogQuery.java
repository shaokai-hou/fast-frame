package com.fast.modules.log.domain.query;

import com.fast.framework.web.PageRequest;
import lombok.Data;

import lombok.EqualsAndHashCode;

/**
 * 登录日志查询参数
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogQuery extends PageRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 状态(0成功 1失败)
     */
    private String status;
}