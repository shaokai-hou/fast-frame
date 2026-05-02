package com.fast.framework.config;

import com.fast.common.enums.SmsProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置
 *
 * @author fast-frame
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

    /**
     * 是否启用短信服务
     */
    private Boolean enabled = false;

    /**
     * 短信服务商（mock/aliyun/tencent）
     */
    private SmsProvider provider = SmsProvider.MOCK;

    /**
     * 短信签名名称
     */
    private String signName;

    /**
     * 短信模板编码
     */
    private String templateCode;

    /**
     * 阿里云 AccessKey ID
     */
    private String accessKeyId;

    /**
     * 阿里云 AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 腾讯云 SecretId
     */
    private String secretId;

    /**
     * 腾讯云 SecretKey
     */
    private String secretKey;

    /**
     * 腾讯云 AppId
     */
    private String appId;
}