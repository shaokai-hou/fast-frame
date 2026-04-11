package com.fast.common.enums;

import lombok.Getter;

/**
 * 菜单类型
 *
 * @author fast-frame
 */
@Getter
public enum MenuType {

    /**
     * 目录
     */
    DIRECTORY("D", "目录"),

    /**
     * 菜单
     */
    MENU("M", "菜单"),

    /**
     * 按钮
     */
    BUTTON("B", "按钮");

    private final String code;
    private final String desc;

    MenuType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static MenuType getByCode(String code) {
        for (MenuType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}