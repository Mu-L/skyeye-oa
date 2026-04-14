/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.websocket.TalkWebSocket;
import com.skyeye.websocket.service.WebSocketMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WebSocketMsgServiceImpl
 * @Description: websocket消息服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/11 20:22
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class WebSocketMsgServiceImpl implements WebSocketMsgService {

    @Autowired
    private TalkWebSocket talkWebSocket;

    @Override
    public void sendWebSocketMsgToUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 发送消息给指定用户
        List<String> userIdList = JSONUtil.toList(params.get("userIdList").toString(), null);
        String msg = params.get("msg").toString();
        Integer messageType = Integer.parseInt(params.get("messageType").toString());
        // 组装消息内容
        String msgContent = JSONUtil.toJsonStr(getMsg(msg, messageType, StrUtil.EMPTY));
        // 发送消息
        for (String userId : userIdList) {
            talkWebSocket.sendMessageTo(msgContent, userId, null);
        }
    }

    @Override
    public void sendWebSocketMsgToAll(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 发送消息给所有用户
        String msg = params.get("msg").toString();
        Integer messageType = Integer.parseInt(params.get("messageType").toString());
        // 组装消息内容
        String msgContent = JSONUtil.toJsonStr(getMsg(msg, messageType, StrUtil.EMPTY));
        // 发送消息
        talkWebSocket.sendMessageToAll(msgContent);
    }

    @Override
    public void sendWebSocketPointMsgToUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        Integer messageType = Integer.parseInt(params.get("messageType").toString());
        log.info("发送不同的websocket消息给指定用户，消息类型：{}", messageType);
        // 发送消息给指定用户
        List<Map<String, Object>> userMsgList = JSONUtil.toList(params.get("userMsgList").toString(), null);
        log.info("用户消息列表：{}", JSONUtil.toJsonStr(userMsgList));
        if (CollectionUtil.isEmpty(userMsgList)) {
            return;
        }
        userMsgList.forEach(userMsg -> {
            String userId = userMsg.get("userId").toString();
            String msg = userMsg.get("msg").toString();
            String itemObject = userMsg.getOrDefault("itemObject", StrUtil.EMPTY).toString();
            // 组装消息内容
            String msgContent = JSONUtil.toJsonStr(getMsg(msg, messageType, itemObject));
            log.info("发送消息给用户：{}，消息内容：{}", userId, msgContent);
            // 发送消息
            talkWebSocket.sendMessageTo(msgContent, userId, null);
        });
    }

    @Override
    public void queryWebSocketRuntimeMetrics(InputObject inputObject, OutputObject outputObject) {
        outputObject.setBean(TalkWebSocket.getRuntimeMetrics());
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private Map<String, Object> getMsg(String message, int messageType, String itemObject) {
        Map<String, Object> result = new HashMap<>();
        result.put("messageType", messageType);
        result.put("message", message);
        result.put("itemObject", itemObject);
        return result;
    }

}
