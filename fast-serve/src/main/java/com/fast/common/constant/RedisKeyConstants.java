package com.fast.common.constant;

/**
 * Redis缓存Key常量
 *
 * @author fast-frame
 */
public class RedisKeyConstants {

    /**
     * 验证码缓存Key前缀
     */
    public static final String CAPTCHA_CODE_PREFIX = "captcha_codes:";

    /**
     * 登录失败次数缓存Key前缀
     */
    public static final String LOGIN_FAIL_PREFIX = "login:fail:";

    /**
     * 登录锁定缓存Key前缀
     */
    public static final String LOGIN_LOCK_PREFIX = "login:lock:";

    /**
     * Sa-Token登录Token缓存Key前缀
     */
    public static final String SA_TOKEN_PREFIX = "sa-token:login:token:";

    /**
     * 字典数据缓存Key前缀
     */
    public static final String DICT_PREFIX = "sys:dict:";

    /**
     * 系统配置缓存Key前缀
     */
    public static final String CONFIG_PREFIX = "sys:config:";
}