package com.fast.modules.auth.service;

/**
 * 短信验证码Service
 *
 * @author fast-frame
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    void sendCode(String phone);

    /**
     * 校验短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否有效
     */
    boolean verifyCode(String phone, String code);
}