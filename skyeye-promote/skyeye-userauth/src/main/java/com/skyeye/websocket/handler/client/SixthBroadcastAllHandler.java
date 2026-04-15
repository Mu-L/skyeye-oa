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
 * 策略：处理全体广播消息（type=Sixth），向所有在线终端发送通知。
 */
public class SixthBroadcastAllHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        Map<String, Object> map1 = SocketConstants.sendAllPeopleMsg(jsonObject);
        socket.sendMessageToAll(JSONUtil.toJsonStr(map1));
    }
}
