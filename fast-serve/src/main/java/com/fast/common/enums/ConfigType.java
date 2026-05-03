package com.fast.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 参数配置类型
 *
 * @author fast-frame
 */
@Getter
@AllArgsConstructor
public enum ConfigType {

    /**
     * 系统内置
     */
    SYSTEM("0", "系统内置"),

    /**
     * 自定义
     */
    CUSTOM("1", "自定义");

    private final String code;
    private final String desc;
}