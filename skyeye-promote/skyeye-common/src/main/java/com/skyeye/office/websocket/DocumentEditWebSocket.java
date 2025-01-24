package com.skyeye.office.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.skyeye.office.service.DocumentEditLogService;
import com.skyeye.office.service.DocumentOnlineUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: DocumentEditWebSocket
 * @Description: 文档协同编辑WebSocket处理器
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Slf4j
@Component
public class DocumentEditWebSocket extends TextWebSocketHandler {

    @Autowired
    private DocumentOnlineUserService documentOnlineUserService;

    @Autowired
    private DocumentEditLogService documentEditLogService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    // 存储文档的所有连接，key为文档ID，value为该文档的所有编辑者session
    private static final Map<String, Map<String, WebSocketSession>> documentSessions = new ConcurrentHashMap<>();

    /**
     * WebSocket连接建立时的处理
     * 1. 将用户加入到会话管理器
     * 2. 记录用户加入状态
     * 3. 广播用户加入消息给其他用户
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            String documentId = getDocumentId(session);
            String userId = getUserId(session);

            // 使用会话管理器管理session
            sessionManager.addSession(documentId, userId, session);
            documentOnlineUserService.userJoin(documentId, userId);
            broadcastUserJoin(documentId, userId);
        } catch (Exception e) {
            log.error("WebSocket连接建立失败", e);
            try {
                session.close();
            } catch (IOException ex) {
                log.error("关闭WebSocket会话失败", ex);
            }
        }
    }

    /**
     * 处理接收到的WebSocket消息
     * 支持的消息类型：
     * 1. EDIT - 文档编辑操作
     * 2. CURSOR - 光标移动
     * 3. SELECTION - 文本选择
     * 4. HEARTBEAT - 心跳检测
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String documentId = getDocumentId(session);
        String userId = getUserId(session);
        
        JSONObject msgData = JSON.parseObject(message.getPayload());
        String type = msgData.getString("type");
        
        // 使用枚举替代字符串
        try {
            switch (MessageType.valueOf(type.toUpperCase())) {
                case EDIT:
                    handleEdit(documentId, userId, msgData);
                    break;
                case CURSOR:
                    handleCursor(documentId, userId, msgData);
                    break;
                case SELECTION:
                    handleSelection(documentId, userId, msgData);
                    break;
                case HEARTBEAT:
                    handleHeartbeat(session);
                    break;
                default:
                    log.warn("未知的消息类型: {}", type);
            }
            // 更新最后活跃时间
            session.getAttributes().put("lastActiveTime", System.currentTimeMillis());
        } catch (IllegalArgumentException e) {
            log.warn("无效的消息类型: {}", type);
        } catch (Exception e) {
            log.error("处理消息失败", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            String documentId = getDocumentId(session);
            String userId = getUserId(session);

            sessionManager.removeSession(documentId, userId);
            documentOnlineUserService.userLeave(documentId, userId);
            broadcastUserLeave(documentId, userId);
        } catch (Exception e) {
            log.error("WebSocket连接关闭处理失败", e);
        }
    }

    /**
     * 处理编辑操作
     * 1. 记录编辑日志
     * 2. 广播编辑操作给其他用户
     */
    private void handleEdit(String documentId, String userId, JSONObject editData) {
        try {
            // 记录编辑日志
            documentEditLogService.addEditLog(documentId, userId, editData);

            // 广播编辑操作给其他用户
            broadcastToOthers(documentId, userId, editData);
        } catch (Exception e) {
            log.error("处理编辑操作失败", e);
        }
    }

    /**
     * 处理光标移动
     */
    private void handleCursor(String documentId, String userId, JSONObject cursorData) {
        try {
            // 广播光标位置给其他用户
            broadcastToOthers(documentId, userId, cursorData);
        } catch (Exception e) {
            log.error("处理光标移动失败", e);
        }
    }

    /**
     * 处理文本选择
     */
    private void handleSelection(String documentId, String userId, JSONObject selectionData) {
        try {
            // 广播选择范围给其他用户
            broadcastToOthers(documentId, userId, selectionData);
        } catch (Exception e) {
            log.error("处理文本选择失败", e);
        }
    }

    /**
     * 广播消息给同一文档的其他用户
     * @param documentId 文档ID
     * @param fromUserId 发送消息的用户ID
     * @param message 消息内容
     */
    private void broadcastToOthers(String documentId, String fromUserId, JSONObject message) {
        try {
            message.put("userId", fromUserId);
            message.put("timestamp", System.currentTimeMillis());
            TextMessage textMessage = new TextMessage(message.toJSONString());
            
            Map<String, WebSocketSession> docSessions = sessionManager.getDocumentSessions(documentId);
            docSessions.forEach((userId, session) -> {
                if (!userId.equals(fromUserId) && session.isOpen()) {
                    try {
                        session.sendMessage(textMessage);
                    } catch (Exception e) {
                        log.error("广播消息失败, userId: {}", userId, e);
                    }
                }
            });
        } catch (Exception e) {
            log.error("广播消息失败", e);
        }
    }

    private void broadcastUserJoin(String documentId, String userId) {
        JSONObject message = new JSONObject();
        message.put("type", "user_join");
        message.put("userId", userId);
        broadcastToOthers(documentId, userId, message);
    }

    private void broadcastUserLeave(String documentId, String userId) {
        JSONObject message = new JSONObject();
        message.put("type", "user_leave");
        message.put("userId", userId);
        broadcastToOthers(documentId, userId, message);
    }

    private String getDocumentId(WebSocketSession session) {
        return session.getAttributes().get("documentId").toString();
    }

    private String getUserId(WebSocketSession session) {
        return session.getAttributes().get("userId").toString();
    }

    /**
     * 处理WebSocket传输错误
     * 出现错误时关闭会话
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket传输错误", exception);
        try {
            session.close();
        } catch (Exception e) {
            log.error("关闭WebSocket会话失败", e);
        }
    }

    /**
     * 处理心跳消息
     * 向客户端发送心跳响应，保持连接活跃
     */
    private void handleHeartbeat(WebSocketSession session) {
        try {
            JSONObject heartbeat = new JSONObject();
            heartbeat.put("type", "heartbeat");
            session.sendMessage(new TextMessage(heartbeat.toJSONString()));
        } catch (Exception e) {
            log.error("发送心跳消息失败", e);
        }
    }
} 