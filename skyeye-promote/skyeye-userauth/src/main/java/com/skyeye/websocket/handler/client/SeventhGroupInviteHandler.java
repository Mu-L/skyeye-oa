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
 * 策略：处理群组邀请消息（type=Seventh），通知被邀请用户。
 */
public class SeventhGroupInviteHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("messageType", type);
        map1.put("toId", jsonObject.getStr("to"));
        socket.sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"), null);
    }
}
