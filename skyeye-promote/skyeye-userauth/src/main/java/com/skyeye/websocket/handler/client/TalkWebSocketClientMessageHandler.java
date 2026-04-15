/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import com.skyeye.websocket.TalkWebSocket;

/**
 * 客户端 WebSocket 业务消息处理器（策略接口，由工厂按 type 分发）
 */
@FunctionalInterface
public interface TalkWebSocketClientMessageHandler {

    /**
     * @param type       消息类型，与 SocketConstants.MessageType 一致
     * @param jsonObject 客户端 JSON 负载
     * @param socket     当前连接实例
     */
    void handle(int type, JSONObject jsonObject, TalkWebSocket socket) throws Exception;
}
