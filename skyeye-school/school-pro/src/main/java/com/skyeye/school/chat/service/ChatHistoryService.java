package com.skyeye.school.chat.service;

import cn.hutool.json.JSONObject;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.ChatHistory;

public interface ChatHistoryService extends SkyeyeBusinessService<ChatHistory> {
    
    String createEntity(JSONObject jsonObject, Integer chatType, Integer readType);

    String createEntity(JSONObject jsonObject, Integer chatType);

    void queryMyChatUnReadMessageList(InputObject inputObject, OutputObject outputObject);

    void editChatHistoryToRead(InputObject inputObject, OutputObject outputObject);

    void queryMyChatMessageList(InputObject inputObject, OutputObject outputObject);
}
