package com.fast.modules.auth.service.sender;

import com.fast.common.enums.SmsProvider;

/**
 * 短信发送策略接口
 *
 * @author fast-frame
 */
public interface SmsSender {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     */
    void send(String phone, String code);

    /**
     * 获取发送器类型
     *
     * @return 类型标识
     */
    SmsProvider getType();
}