package com.skyeye.office.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: WebSocketSessionManager
 * @Description: WebSocket会话管理器
 * 负责管理所有WebSocket连接，提供：
 * 1. 会话的添加、移除和获取
 * 2. 会话状态的检查和维护
 * 3. 超时会话的清理
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Slf4j
@Component
public class WebSocketSessionManager {
    
    /**
     * 存储所有WebSocket会话
     * 外层Map的key为文档ID，value为该文档的所有会话
     * 内层Map的key为用户ID，value为该用户的WebSocket会话
     */
    private static final Map<String, Map<String, WebSocketSession>> documentSessions = new ConcurrentHashMap<>();
    
    /**
     * 添加WebSocket会话
     * @param documentId 文档ID
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    public void addSession(String documentId, String userId, WebSocketSession session) {
        documentSessions.computeIfAbsent(documentId, k -> new ConcurrentHashMap<>())
            .put(userId, session);
    }
    
    /**
     * 移除WebSocket会话
     * 如果文档的所有会话都被移除，则同时移除文档记录
     * @param documentId 文档ID
     * @param userId 用户ID
     */
    public void removeSession(String documentId, String userId) {
        Map<String, WebSocketSession> docSessions = documentSessions.get(documentId);
        if (docSessions != null) {
            docSessions.remove(userId);
            if (docSessions.isEmpty()) {
                documentSessions.remove(documentId);
            }
        }
    }
    
    /**
     * 获取指定文档的所有会话
     * @param documentId 文档ID
     * @return 文档的所有会话映射表，如果不存在则返回空Map
     */
    public Map<String, WebSocketSession> getDocumentSessions(String documentId) {
        return documentSessions.getOrDefault(documentId, new ConcurrentHashMap<>());
    }

    /**
     * 获取指定文档的在线用户数
     */
    public int getOnlineCount(String documentId) {
        Map<String, WebSocketSession> docSessions = documentSessions.get(documentId);
        return docSessions != null ? docSessions.size() : 0;
    }

    /**
     * 关闭指定文档的所有会话
     */
    public void closeDocumentSessions(String documentId) {
        Map<String, WebSocketSession> docSessions = documentSessions.get(documentId);
        if (docSessions != null) {
            docSessions.values().forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.close();
                    }
                } catch (Exception e) {
                    log.error("关闭WebSocket会话失败", e);
                }
            });
            documentSessions.remove(documentId);
        }
    }

    /**
     * 检查会话是否有效
     * @param documentId 文档ID
     * @param userId 用户ID
     * @return true-会话有效且开启状态，false-会话无效或已关闭
     */
    public boolean isSessionValid(String documentId, String userId) {
        Map<String, WebSocketSession> docSessions = documentSessions.get(documentId);
        if (docSessions != null) {
            WebSocketSession session = docSessions.get(userId);
            return session != null && session.isOpen();
        }
        return false;
    }

    /**
     * 清理超时会话
     * @param timeoutMillis 超时时间（毫秒）
     * 超过指定时间未活动的会话将被关闭并移除
     */
    public void cleanTimeoutSessions(long timeoutMillis) {
        long now = System.currentTimeMillis();
        documentSessions.forEach((documentId, sessions) -> {
            sessions.forEach((userId, session) -> {
                Long lastActiveTime = (Long) session.getAttributes().get("lastActiveTime");
                if (lastActiveTime != null && now - lastActiveTime > timeoutMillis) {
                    try {
                        session.close();
                        removeSession(documentId, userId);
                    } catch (Exception e) {
                        log.error("关闭超时会话失败", e);
                    }
                }
            });
        });
    }
} 