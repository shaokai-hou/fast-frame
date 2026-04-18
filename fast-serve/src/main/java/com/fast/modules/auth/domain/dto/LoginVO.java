package com.fast.modules.auth.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应VO
 *
 * @author fast-frame
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;
}