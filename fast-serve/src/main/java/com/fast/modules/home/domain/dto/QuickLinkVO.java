package com.fast.modules.home.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 快捷入口VO
 *
 * @author fast-frame
 */
@Data
public class QuickLinkVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 菜单图标
     */
    private String icon;
}