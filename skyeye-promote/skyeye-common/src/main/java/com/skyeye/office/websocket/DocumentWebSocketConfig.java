package com.skyeye.office.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @ClassName: DocumentWebSocketConfig
 * @Description: WebSocket配置类，用于注册WebSocket处理器和配置WebSocket连接
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Configuration
@EnableWebSocket
public class DocumentWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DocumentEditWebSocket documentEditWebSocket;

    /**
     * 注册WebSocket处理器
     * 配置WebSocket连接路径和跨域设置
     * @param registry WebSocket处理器注册表
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(documentEditWebSocket, "/websocket/document/edit")
            .setAllowedOrigins("*");  // 允许所有来源的WebSocket连接
    }
} 