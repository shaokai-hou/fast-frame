package com.fast.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信服务商类型
 *
 * @author fast-frame
 */
@Getter
@AllArgsConstructor
public enum SmsProvider {

    /**
     * Mock模式（日志输出）
     */
    MOCK("mock"),

    /**
     * 阿里云短信
     */
    ALIYUN("aliyun"),

    /**
     * 腾讯云短信
     */
    TENCENT("tencent");

    private final String code;

    /**
     * 根据code获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static SmsProvider fromCode(String code) {
        for (SmsProvider provider : values()) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        return MOCK;
    }
}