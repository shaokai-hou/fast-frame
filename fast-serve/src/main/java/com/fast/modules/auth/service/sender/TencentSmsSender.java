package com.fast.modules.auth.service.sender;

import com.fast.common.exception.BusinessException;
import com.fast.common.enums.SmsProvider;
import com.fast.framework.config.SmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 腾讯云短信发送器
 *
 * @author fast-frame
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TencentSmsSender implements SmsSender {

    private final SmsProperties smsProperties;

    private volatile SmsClient client;

    @Override
    public void send(String phone, String code) {
        try {
            SmsClient client = getClient();
            com.tencentcloudapi.sms.v20210111.models.SendSmsRequest request =
                    new com.tencentcloudapi.sms.v20210111.models.SendSmsRequest();
            request.setSmsSdkAppId(smsProperties.getAppId());
            request.setSignName(smsProperties.getSignName());
            request.setTemplateId(smsProperties.getTemplateCode());
            request.setPhoneNumberSet(new String[]{"+86" + phone});
            request.setTemplateParamSet(new String[]{code});

            com.tencentcloudapi.sms.v20210111.models.SendSmsResponse response = client.SendSms(request);
            SendStatus[] statuses = response.getSendStatusSet();
            if (statuses != null && statuses.length > 0) {
                SendStatus status = statuses[0];
                if ("Ok".equals(status.getCode())) {
                    log.info("腾讯云短信发送成功 - 手机号: {}", phone);
                } else {
                    log.error("腾讯云短信发送失败 - 手机号: {}, 错误码: {}, 错误信息: {}",
                            phone, status.getCode(), status.getMessage());
                    throw new BusinessException("短信发送失败");
                }
            }
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信发送异常 - 手机号: {}", phone, e);
            throw new BusinessException("短信发送失败: " + e.getMessage());
        }
    }

    @Override
    public SmsProvider getType() {
        return SmsProvider.TENCENT;
    }

    private SmsClient getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    Credential cred = new Credential(smsProperties.getSecretId(), smsProperties.getSecretKey());
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("sms.tencentcloudapi.com");
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    client = new SmsClient(cred, "ap-guangzhou", clientProfile);
                }
            }
        }
        return client;
    }
}