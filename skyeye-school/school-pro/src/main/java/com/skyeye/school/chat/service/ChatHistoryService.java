package com.skyeye.school.chat.service;

import cn.hutool.json.JSONObject;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.ChatHistory;

public interface ChatHistoryService extends SkyeyeBusinessService<ChatHistory> {
    void queryChatHistoryByUniqueId(InputObject inputObject, OutputObject outputObject);

    void deleteChatHistoryByUniqueId(InputObject inputObject, OutputObject outputObject);

    void deleteChatHistoryById(InputObject inputObject, OutputObject outputObject);

    String createEntity(JSONObject jsonObject, Integer chatType, Integer readType);

    String createEntity(JSONObject jsonObject, Integer chatType);

}
