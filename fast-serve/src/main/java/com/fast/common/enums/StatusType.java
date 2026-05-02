package com.fast.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态
 *
 * @author fast-frame
 */
@Getter
@AllArgsConstructor
public enum StatusType {

    /**
     * 正常
     */
    NORMAL("0", "正常"),

    /**
     * 禁用
     */
    DISABLE("1", "禁用");

    private final String code;
    private final String desc;

    /**
     * 根据code获取枚举
     *
     * @param code 编码
     * @return 枚举值
     */
    public static StatusType fromCode(String code) {
        for (StatusType status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NORMAL;
    }

    /**
     * 是否正常
     *
     * @return true-正常
     */
    public boolean isNormal() {
        return this == NORMAL;
    }

    /**
     * 是否禁用
     *
     * @return true-禁用
     */
    public boolean isDisable() {
        return this == DISABLE;
    }
}