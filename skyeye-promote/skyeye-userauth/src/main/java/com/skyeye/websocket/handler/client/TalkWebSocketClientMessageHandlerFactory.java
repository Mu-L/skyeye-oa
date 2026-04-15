/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.websocket.TalkWebSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端消息处理器工厂：按 type 注册策略，避免 TalkWebSocket 内大量 if-else。
 */
@Slf4j
public final class TalkWebSocketClientMessageHandlerFactory {

    private static final Map<Integer, TalkWebSocketClientMessageHandler> HANDLERS = new ConcurrentHashMap<>();

    static {
        Map<Integer, TalkWebSocketClientMessageHandler> map = new HashMap<>();
        map.put(SocketConstants.MessageType.Fourth.getType(), new FourthPersonalChatHandler());
        map.put(SocketConstants.MessageType.Fifth.getType(), new FifthSystemMessageHandler());
        map.put(SocketConstants.MessageType.Sixth.getType(), new SixthBroadcastAllHandler());
        map.put(SocketConstants.MessageType.Seventh.getType(), new SeventhGroupInviteHandler());
        map.put(SocketConstants.MessageType.Eighth.getType(), new EighthStealthHandler());
        map.put(SocketConstants.MessageType.Ninth.getType(), new NinthStealthOnlineHandler());
        map.put(SocketConstants.MessageType.Tenth.getType(), new TenthAgreeJoinGroupHandler());
        map.put(SocketConstants.MessageType.Eleventh.getType(), new EleventhGroupChatHandler());
        map.put(SocketConstants.MessageType.Twelfth.getType(), new TwelfthQuitGroupHandler());
        map.put(SocketConstants.MessageType.Thirteenth.getType(), new ThirteenthDisbandGroupHandler());
        HANDLERS.putAll(map);
    }

    private TalkWebSocketClientMessageHandlerFactory() {
    }

    public static void dispatch(int type, JSONObject jsonObject, TalkWebSocket socket) throws Exception {
        TalkWebSocketClientMessageHandler handler = HANDLERS.get(type);
        if (handler == null) {
            log.warn("未注册的客户端消息类型: {}, payload: {}", type, jsonObject);
            return;
        }
        handler.handle(type, jsonObject, socket);
    }
}
