package com.fast.modules.auth.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码VO
 *
 * @author fast-frame
 */
@Data
public class CaptchaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 验证码UUID
     */
    private String uuid;

    /**
     * 验证码图片(Base64)
     */
    private String img;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
}