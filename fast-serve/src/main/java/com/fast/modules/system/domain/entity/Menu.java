package com.fast.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fast.framework.web.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体
 *
 * @author fast-frame
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class Menu extends BaseEntity {

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单类型(D目录 M菜单 B按钮)
     */
    private String menuType;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 外链地址(iframe页面使用)
     */
    private String link;

    /**
     * 显示顺序
     */
    private Integer menuSort;

    /**
     * 是否显示(0显示 1隐藏)
     */
    private String visible;

    /**
     * 状态(0正常 1禁用)
     */
    private String status;
}