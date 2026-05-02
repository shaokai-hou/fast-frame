package com.fast.framework.websocket;

import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket Session 包装器
 *
 * @author fast-frame
 */
public class WebSocketSessionWrapper {

    /**
     * 用户ID
     */
    private final Long userId;

    /**
     * WebSocket Session
     */
    private final WebSocketSession session;

    public WebSocketSessionWrapper(Long userId, WebSocketSession session) {
        this.userId = userId;
        this.session = session;
    }

    public Long getUserId() {
        return userId;
    }

    public WebSocketSession getSession() {
        return session;
    }
}