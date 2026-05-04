package com.fast.framework.annotation;

import java.lang.annotation.*;

/**
 * 接口防抖注解
 * 在指定窗口期内，同一用户对同一接口的重复请求将被拦截
 *
 * @author fast-frame
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Debounce {

    /**
     * 防抖窗口期（秒）
     * 默认3秒内不允许重复请求
     */
    int interval() default 3;

    /**
     * 防抖Key后缀（可选）
     * 用于区分同一接口的不同业务场景
     * 例如: @Debounce(suffix = "save") 和 @Debounce(suffix = "update")
     */
    String suffix() default "";
}