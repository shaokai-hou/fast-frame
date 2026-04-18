package com.fast.modules.auth.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 路由VO
 *
 * @author fast-frame
 */
@Data
public class RoutesVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路由列表
     */
    private List<RouteItem> routes;

    @Data
    public static class RouteItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 路由路径
         */
        private String path;

        /**
         * 路由名称
         */
        private String name;

        /**
         * 组件路径
         */
        private String component;

        /**
         * 路由元信息
         */
        private RouteMeta meta;

        /**
         * 子路由
         */
        private List<RouteItem> children;
    }

    @Data
    public static class RouteMeta implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 标题
         */
        private String title;

        /**
         * 图标
         */
        private String icon;

        /**
         * 是否隐藏
         */
        private Boolean hidden;

        /**
         * 外链地址(iframe页面使用)
         */
        private String link;
    }
}