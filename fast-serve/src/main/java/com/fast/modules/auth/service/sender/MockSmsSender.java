package com.fast.modules.auth.service.sender;

import com.fast.common.enums.SmsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock短信发送器（日志输出）
 *
 * @author fast-frame
 */
@Slf4j
@Component
public class MockSmsSender implements SmsSender {

    @Override
    public void send(String phone, String code) {
        log.info("【短信验证码】手机号: {}, 验证码: {}", phone, code);
    }

    @Override
    public SmsProvider getType() {
        return SmsProvider.MOCK;
    }
}