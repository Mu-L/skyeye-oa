package com.skyeye.websocket.listener;

import com.skyeye.websocket.TalkWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * WebSocket跨节点分发消息订阅器
 */
@Slf4j
@Component
public class TalkWebSocketClusterSubscriber implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);
            TalkWebSocket.handleClusterDispatch(payload);
        } catch (Exception e) {
            log.error("处理Redis WebSocket分发消息失败: {}", e.getMessage(), e);
        }
    }
}
