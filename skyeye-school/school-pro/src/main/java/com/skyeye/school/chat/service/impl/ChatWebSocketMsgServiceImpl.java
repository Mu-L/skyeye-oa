package com.skyeye.school.chat.service.impl;

import com.skyeye.school.chat.entity.ChatWebSocket;
import com.skyeye.school.chat.service.ChatWebSocketMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatWebSocketMsgServiceImpl implements ChatWebSocketMsgService {

    @Autowired
    private ChatWebSocket chatWebSocket;

}
