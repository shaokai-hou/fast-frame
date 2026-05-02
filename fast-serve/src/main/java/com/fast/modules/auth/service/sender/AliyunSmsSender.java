package com.fast.modules.auth.service.sender;

import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.fast.common.exception.BusinessException;
import com.fast.common.enums.SmsProvider;
import com.fast.framework.config.SmsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信发送器
 *
 * @author fast-frame
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunSmsSender implements SmsSender {

    private final SmsProperties smsProperties;

    private volatile Client client;

    @Override
    public void send(String phone, String code) {
        try {
            Client client = getClient();
            Map<String, String> templateParam = new HashMap<>(1);
            templateParam.put("code", code);

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setTemplateParam(JSONUtil.toJsonStr(templateParam));

            SendSmsResponse response = client.sendSms(request);
            if ("OK".equals(response.getBody().getCode())) {
                log.info("阿里云短信发送成功 - 手机号: {}", phone);
            } else {
                log.error("阿里云短信发送失败 - 手机号: {}, 错误码: {}, 错误信息: {}",
                        phone, response.getBody().getCode(), response.getBody().getMessage());
                throw new BusinessException("短信发送失败");
            }
        } catch (Exception e) {
            log.error("阿里云短信发送异常 - 手机号: {}", phone, e);
            throw new BusinessException("短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public SmsProvider getType() {
        return SmsProvider.ALIYUN;
    }

    private Client getClient() throws Exception {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Config config = new Config()
                            .setAccessKeyId(smsProperties.getAccessKeyId())
                            .setAccessKeySecret(smsProperties.getAccessKeySecret())
                            .setEndpoint("dysmsapi.aliyuncs.com");
                    client = new Client(config);
                }
            }
        }
        return client;
    }
}