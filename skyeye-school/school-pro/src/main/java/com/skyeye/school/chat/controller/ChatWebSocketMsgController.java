package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.school.chat.service.ChatWebSocketMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "chatwebsocket消息处理", tags = "chatwebsocket消息处理", modelName = "chatwebsocket消息处理")
public class ChatWebSocketMsgController {

    @Autowired
    private ChatWebSocketMsgService chatWebSocketMsgService;

}
