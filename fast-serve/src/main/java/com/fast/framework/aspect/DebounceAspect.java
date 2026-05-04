package com.fast.framework.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.fast.common.constant.RedisConstants;
import com.fast.common.exception.BusinessException;
import com.fast.framework.annotation.Debounce;
import com.fast.framework.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 接口防抖切面
 * 通过 Redis SETNX 实现防抖，在指定窗口期内拦截重复请求
 *
 * @author fast-frame
 */
@Slf4j
@Aspect
@Component
public class DebounceAspect {

    /**
     * 防抖拦截
     *
     * @param joinPoint 切点
     * @param debounce  防抖注解
     */
    @Before("@annotation(debounce)")
    public void doBefore(JoinPoint joinPoint, Debounce debounce) {
        // 获取当前用户ID
        Long userId = StpUtil.getLoginIdAsLong();

        // 获取类名和方法名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // 构建防抖Key: debounce:{userId}:{className}:{methodName}:{suffix}
        String suffix = debounce.suffix();
        StringBuilder keyBuilder = new StringBuilder(RedisConstants.DEBOUNCE_PREFIX);
        keyBuilder.append(userId).append(":");
        keyBuilder.append(className).append(":");
        keyBuilder.append(methodName);
        if (!suffix.isEmpty()) {
            keyBuilder.append(":").append(suffix);
        }
        String key = keyBuilder.toString();

        // 使用 SETNX 设置防抖标记，窗口期为 interval 秒
        int interval = debounce.interval();
        boolean success = RedisHelper.setIfAbsent(key, "1", interval);

        // 如果标记已存在，说明在窗口期内已执行过，拒绝本次请求
        if (!success) {
            log.warn("[Debounce] 重复请求被拦截: userId={}, key={}", userId, key);
            throw new BusinessException("操作过于频繁，请稍后再试");
        }

        log.debug("[Debounce] 防抖标记已设置: key={}, interval={}s", key, interval);
    }
}