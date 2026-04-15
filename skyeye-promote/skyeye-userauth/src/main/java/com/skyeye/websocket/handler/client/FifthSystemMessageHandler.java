/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import com.skyeye.websocket.TalkWebSocket;
import lombok.extern.slf4j.Slf4j;

/**
 * 策略：处理系统消息（type=Fifth），当前用于记录系统消息日志。
 */
@Slf4j
public class FifthSystemMessageHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        log.info("收到系统消息: {}", jsonObject);
    }
}
