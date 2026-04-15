/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.websocket.TalkWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略：处理普通单聊消息（type=Fourth），负责入库并向接收方/发送方终端推送。
 */
@Slf4j
public class FourthPersonalChatHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        TransactionTemplate transactionTemplate = SpringUtils.getBean(TransactionTemplate.class);
        transactionTemplate.execute(status -> {
            try {
                TalkChatHistoryService talkChatHistoryService = SpringUtils.getBean(TalkChatHistoryService.class);
                String toUserId = jsonObject.getStr("to");
                String id = talkChatHistoryService.createEntity(jsonObject, TalkChatType.PERSONAL_TO_PERSONAL.getKey(),
                    WhetherEnum.DISABLE_USING.getKey());
                if (TalkWebSocket.isUserOnline(toUserId)) {
                    Map<String, Object> finalMap = new HashMap<>();
                    finalMap.put("messageType", type);
                    finalMap.put("dataId", id);
                    finalMap.putAll(SocketConstants.sendOrdinaryMsg(jsonObject));
                    socket.sendMessageTo(JSONUtil.toJsonStr(finalMap), toUserId, null);
                }
                socket.sendMessageToMe(jsonObject.getStr("message"), toUserId, type);
                return true;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("发送个人消息失败: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
}
