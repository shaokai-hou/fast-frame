package com.fast.modules.auth.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录DTO
 *
 * @author fast-frame
 */
@Data
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 滑块验证码校验参数
     */
    private String captchaVerification;

    /**
     * 记住密码
     */
    private Boolean rememberMe = false;
}