/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.websocket.TalkWebSocket;

import java.util.Map;

/**
 * 策略：处理解散群聊消息（type=Thirteenth），向群相关成员广播解散通知。
 */
public class ThirteenthDisbandGroupHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        Map<String, Object> map1 = SocketConstants.sendDisbandGroupToAllMsg(jsonObject);
        socket.sendMessageToAll(JSONUtil.toJsonStr(map1));
    }
}
