package com.fast.framework.helper;

import cn.hutool.extra.spring.SpringUtil;
import com.fast.modules.system.service.ConfigService;

/**
 * 系统配置辅助类
 * 提供便捷的配置获取静态方法
 *
 * @author fast-frame
 */
public class ConfigHelper {

    private static ConfigService getConfigService() {
        return SpringUtil.getBean(ConfigService.class);
    }

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @return 配置值，不存在返回 null
     */
    public static String getValue(String configKey) {
        return getConfigService().getConfigValue(configKey);
    }

    /**
     * 获取配置值（带默认值）
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getValue(String configKey, String defaultValue) {
        String value = getValue(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整数配置值
     *
     * @param configKey 配置键
     * @return 整数配置值，不存在或转换失败返回 null
     */
    public static Integer getIntValue(String configKey) {
        String value = getValue(configKey);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取整数配置值（带默认值）
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 整数配置值
     */
    public static int getIntValue(String configKey, int defaultValue) {
        Integer value = getIntValue(configKey);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取布尔配置值
     *
     * @param configKey 配置键
     * @return 布尔配置值，不存在返回 null
     */
    public static Boolean getBooleanValue(String configKey) {
        String value = getValue(configKey);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * 获取布尔配置值（带默认值）
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 布尔配置值
     */
    public static boolean getBooleanValue(String configKey, boolean defaultValue) {
        Boolean value = getBooleanValue(configKey);
        return value != null ? value : defaultValue;
    }
}