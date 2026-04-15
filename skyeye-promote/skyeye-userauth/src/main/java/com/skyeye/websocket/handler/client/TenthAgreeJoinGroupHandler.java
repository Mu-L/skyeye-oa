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
 * 策略：处理入群审核通过通知（type=Tenth），通知目标用户刷新群信息。
 */
public class TenthAgreeJoinGroupHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        Map<String, Object> map1 = SocketConstants.sendAgreeJoinGroupMsg(jsonObject);
        socket.sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"), null);
    }
}
