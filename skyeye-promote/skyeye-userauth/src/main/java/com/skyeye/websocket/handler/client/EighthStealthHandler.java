/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.websocket.TalkWebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略：处理隐身状态消息（type=Eighth），广播用户隐身状态变更。
 */
public class EighthStealthHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("messageType", type);
        map1.put("userId", jsonObject.getStr("userId"));
        socket.sendMessageToAll(JSONUtil.toJsonStr(map1));
    }
}
