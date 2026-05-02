package com.fast.framework.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息 WebSocket 处理器
 *
 * @author fast-frame
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageWebSocketHandler implements WebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            WebSocketSessionWrapper wrapper = new WebSocketSessionWrapper(userId, session);
            sessionManager.addSession(userId, wrapper);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 处理客户端消息（可选：心跳检测等）
        if (message instanceof TextMessage) {
            String payload = ((TextMessage) message).getPayload();
            log.debug("收到WebSocket消息: {}", payload);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 发送新消息通知
     *
     * @param userId      接收人ID
     * @param messageId   消息ID
     * @param title       消息标题
     * @param senderName  发送人名称
     * @param priority    优先级
     */
    public void sendNewMessageNotification(Long userId, Long messageId, String title, String senderName, String priority) {
        WebSocketSessionWrapper wrapper = sessionManager.getSession(userId);
        if (wrapper == null || !wrapper.getSession().isOpen()) {
            return;
        }

        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NEW_MESSAGE");

            Map<String, Object> data = new HashMap<>();
            data.put("id", messageId);
            data.put("title", title);
            data.put("senderName", senderName);
            data.put("priority", priority);
            notification.put("data", data);

            String json = objectMapper.writeValueAsString(notification);
            wrapper.getSession().sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error("发送WebSocket消息失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 发送未读数量更新通知
     *
     * @param userId       用户ID
     * @param unreadCount  未读数量
     */
    public void sendUnreadCountUpdate(Long userId, Long unreadCount) {
        WebSocketSessionWrapper wrapper = sessionManager.getSession(userId);
        if (wrapper == null || !wrapper.getSession().isOpen()) {
            return;
        }

        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "UNREAD_COUNT_UPDATE");

            Map<String, Object> data = new HashMap<>();
            data.put("unreadCount", unreadCount);
            notification.put("data", data);

            String json = objectMapper.writeValueAsString(notification);
            wrapper.getSession().sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error("发送未读数量更新失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 批量发送新消息通知
     *
     * @param userIds     接收人ID列表
     * @param messageId   消息ID
     * @param title       消息标题
     * @param senderName  发送人名称
     * @param priority    优先级
     */
    public void broadcastNewMessage(List<Long> userIds, Long messageId, String title, String senderName, String priority) {
        for (Long userId : userIds) {
            sendNewMessageNotification(userId, messageId, title, senderName, priority);
        }
    }
}