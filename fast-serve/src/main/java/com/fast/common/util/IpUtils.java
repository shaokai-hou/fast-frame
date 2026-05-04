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

    /**
     * 判断 IP 是否在 CIDR 范围内
     * 支持 IPv4 CIDR 格式如 192.168.1.0/24
     *
     * @param ip   待检查的 IP 地址
     * @param cidr CIDR 格式的网段（如 192.168.1.0/24）
     * @return 是否属于该网段
     */
    public static boolean isIpInCidr(String ip, String cidr) {
        if (StrUtil.isEmpty(ip) || StrUtil.isEmpty(cidr)) {
            return false;
        }

        // 解析 CIDR
        String[] cidrParts = cidr.split("/");
        if (cidrParts.length != 2) {
            // 如果不是 CIDR 格式，按单 IP 匹配
            return ip.equals(cidr);
        }

        String networkAddress = cidrParts[0];
        int prefixLength = Integer.parseInt(cidrParts[1]);

        // IPv4 前缀长度范围 0-32
        if (prefixLength < 0 || prefixLength > 32) {
            return false;
        }

        try {
            long ipLong = ipToLong(ip);
            long networkLong = ipToLong(networkAddress);

            // 计算掩码
            long mask = 0xFFFFFFFFL << (32 - prefixLength);

            // 判断 IP 是否在网段内
            return (ipLong & mask) == (networkLong & mask);
        } catch (Exception e) {
            // IP 格式不正确，按单 IP 匹配
            return ip.equals(cidr);
        }
    }

    /**
     * 将 IPv4 地址转换为长整型
     *
     * @param ip IPv4 地址字符串
     * @return 长整型表示
     */
    private static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }

        long result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (Long.parseLong(parts[i]) << (24 - i * 8));
        }
        return result;
    }
}