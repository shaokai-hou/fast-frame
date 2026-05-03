package com.fast.modules.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.common.enums.SmsProvider;
import com.fast.framework.config.SmsProperties;
import com.fast.framework.helper.RedisHelper;
import com.fast.modules.auth.service.SmsService;
import com.fast.modules.auth.service.sender.SmsSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 短信验证码Service实现
 *
 * @author fast-frame
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final SmsProperties smsProperties;
    private final List<SmsSender> smsSenders;
    private Map<String, SmsSender> senderMap;

    /**
     * 验证码有效期（分钟）
     */
    private static final int CODE_EXPIRE_MINUTES = 5;
    /**
     * 发送频率限制（秒）
     */
    private static final int SEND_INTERVAL_SECONDS = 60;

    /**
     * 初始化发送器映射
     */
    @PostConstruct
    public void init() {
        senderMap = smsSenders.stream()
                .collect(Collectors.toMap(sender -> sender.getType().getCode(), Function.identity()));
    }

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    @Override
    public void sendCode(String phone) {
        // 检查发送频率限制
        String limitKey = RedisConstants.SMS_LIMIT_PREFIX + phone;
        if (RedisHelper.hasKey(limitKey)) {
            long remaining = RedisHelper.getExpire(limitKey);
            throw new BusinessException("验证码发送过于频繁，请" + remaining + "秒后重试");
        }

        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);

        // 存储验证码到Redis（有效期5分钟）
        String codeKey = RedisConstants.SMS_CODE_PREFIX + phone;
        RedisHelper.set(codeKey, code, CODE_EXPIRE_MINUTES * 60);

        // 设置发送频率限制（60秒）
        RedisHelper.set(limitKey, "1", SEND_INTERVAL_SECONDS);

        // 检查短信服务是否启用
        if (!smsProperties.getEnabled()) {
            throw new BusinessException("短信服务未启用");
        }

        // 根据provider获取发送器
        SmsSender sender = getSender(smsProperties.getProvider());
        sender.send(phone, code);
    }

    /**
     * 校验短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否有效
     */
    @Override
    public boolean verifyCode(String phone, String code) {
        String codeKey = RedisConstants.SMS_CODE_PREFIX + phone;
        Object cachedCode = RedisHelper.get(codeKey);
        if (Objects.isNull(cachedCode)) {
            return false;
        }
        // 验证成功后删除验证码（一次性使用）
        if (cachedCode.toString().equals(code)) {
            RedisHelper.delete(codeKey);
            return true;
        }
        return false;
    }

    /**
     * 根据类型获取发送器
     *
     * @param provider 服务商类型
     * @return 发送器
     */
    private SmsSender getSender(SmsProvider provider) {
        return senderMap.getOrDefault(provider.getCode(), senderMap.get(SmsProvider.MOCK.getCode()));
    }
}