package com.fast.framework.websocket;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 *
 * @author fast-frame
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");

            if (token == null || token.isEmpty()) {
                log.warn("WebSocket握手失败: 缺少token参数");
                return false;
            }

            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                log.warn("WebSocket握手失败: token无效");
                return false;
            }

            Long userId = Long.parseLong(loginId.toString());
            attributes.put("userId", userId);
            log.info("WebSocket握手成功: userId={}", userId);
            return true;
        } catch (Exception e) {
            log.error("WebSocket握手异常: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手后异常: {}", exception.getMessage());
        }
    }
}