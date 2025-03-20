package com.skyeye.school.chat.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.ChatWebSocket;
import com.skyeye.school.chat.service.ChatWebSocketMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatWebSocketMsgServiceImpl implements ChatWebSocketMsgService {

    @Autowired
    private ChatWebSocket chatWebSocket;
    @Override
    public void sendChatWebSocketMsgToUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 发送消息给指定用户
        List<String> userIdList = JSONUtil.toList(params.get("userIdList").toString(), null);
        String msg = params.get("msg").toString();
        Integer messageType = Integer.parseInt(params.get("messageType").toString());
        // 组装消息内容
        String msgContent = JSONUtil.toJsonStr(getMsg(msg, messageType));
        // 发送消息
        for (String userId : userIdList) {
            chatWebSocket.sendMessageTo(msgContent, userId, null);
        }
    }

    @Override
    public void sendChatWebSocketMsgToAll(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // 发送消息给所有用户
        String msg = params.get("msg").toString();
        Integer messageType = Integer.parseInt(params.get("messageType").toString());
        // 组装消息内容
        String msgContent = JSONUtil.toJsonStr(getMsg(msg, messageType));
        // 发送消息
        chatWebSocket.sendMessageToAll(msgContent);
    }

    private Map<String, Object> getMsg(String message, int messageType) {
        Map<String, Object> result = new HashMap<>();
        result.put("messageType", messageType);
        result.put("message", message);
        return result;
    }
}
