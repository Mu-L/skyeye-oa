package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.school.chat.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "聊天历史管理", tags = "聊天历史管理", modelName = "聊天历史管理")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;
}
