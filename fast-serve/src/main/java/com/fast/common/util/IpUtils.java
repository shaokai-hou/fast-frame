package com.fast.common.util;

import cn.hutool.core.util.StrUtil;
import javax.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 * @author haohao
 */
public final class IpUtils {

    private static final String UNKNOWN = "unknown";

    private IpUtils() {
        // 私有构造，禁止实例化
    }

    /**
     * 获取客户端真实IP地址
     * 支持多种代理场景：Nginx、Apache、Squid等
     *
     * @param request HTTP请求对象
     * @return 客户端IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index).trim();
            }
            return ip;
        }

        ip = request.getHeader("X-Real-IP");
        if (StrUtil.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (StrUtil.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (StrUtil.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)) {
            return ip;
        }

        return request.getRemoteAddr();
    }
}