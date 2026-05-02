package com.fast.common.util;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * IP地址工具类
 *
 * @author haohao
 */
public final class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String IPV6_LOCALHOST = "0:0:0:0:0:0:0:1";
    private static final String IPV4_LOCALHOST = "127.0.0.1";

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

        ip = request.getRemoteAddr();
        // 将 IPv6 localhost 转换为 IPv4 格式，显示更友好
        if (IPV6_LOCALHOST.equals(ip)) {
            return IPV4_LOCALHOST;
        }
        return ip;
    }

    /**
     * 获取客户端浏览器信息
     * 用于验证码服务识别客户端
     *
     * @param request HTTP请求对象
     * @return 浏览器信息(ip + ua)
     */
    public static String getBrowserInfo(HttpServletRequest request) {
        String ip = getClientIp(request);
        String ua = request.getHeader("user-agent");
        return ip + ua;
    }
}