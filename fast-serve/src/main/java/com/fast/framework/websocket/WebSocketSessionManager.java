package com.fast.framework.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Session 管理器
 *
 * @author fast-frame
 */
@Slf4j
@Component
public class WebSocketSessionManager {

    /**
     * 用户ID与Session的映射（支持一个用户多个连接）
     */
    private final Map<Long, WebSocketSessionWrapper> sessions = new ConcurrentHashMap<>();

    /**
     * 添加Session
     *
     * @param userId  用户ID
     * @param session Session包装器
     */
    public void addSession(Long userId, WebSocketSessionWrapper session) {
        sessions.put(userId, session);
        log.info("WebSocket连接添加: userId={}", userId);
    }

    /**
     * 移除Session
     *
     * @param userId 用户ID
     */
    public void removeSession(Long userId) {
        sessions.remove(userId);
        log.info("WebSocket连接移除: userId={}", userId);
    }

    /**
     * 获取Session
     *
     * @param userId 用户ID
     * @return Session包装器
     */
    public WebSocketSessionWrapper getSession(Long userId) {
        return sessions.get(userId);
    }

    /**
     * 判断用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isOnline(Long userId) {
        return sessions.containsKey(userId);
    }

    /**
     * 获取所有在线用户ID
     *
     * @return 用户ID集合
     */
    public java.util.Set<Long> getOnlineUserIds() {
        return sessions.keySet();
    }

    /**
     * 获取在线用户数量
     *
     * @return 在线用户数量
     */
    public int getOnlineCount() {
        return sessions.size();
    }
}