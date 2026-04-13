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
     * Sa-Token v1.37.0 默认前缀格式为 sa-token: (带中划线)
     */
    public static final String SA_TOKEN_PREFIX = "sa-token:login:token:";

    /**
     * Sa-Token用户Session缓存Key前缀
     * 存储用户会话数据，key格式: sa-token:login:session:{loginId}
     */
    public static final String SA_TOKEN_SESSION_PREFIX = "sa-token:login:session:";

    /**
     * 字典数据缓存Key前缀
     */
    public static final String DICT_PREFIX = "sys:dict:";

    /**
     * 系统配置缓存Key前缀
     */
    public static final String CONFIG_PREFIX = "sys:config:";
}