/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.school.chat.service;

import cn.hutool.json.JSONObject;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.ChatHistory;

import java.util.List;
import java.util.Map;

public interface ChatHistoryService extends SkyeyeBusinessService<ChatHistory> {

    String createEntity(JSONObject jsonObject, Integer chatType, Integer readType);

    String createEntity(JSONObject jsonObject, Integer chatType);

    void queryMyChatUnReadMessageList(InputObject inputObject, OutputObject outputObject);

    void editChatHistoryToRead(InputObject inputObject, OutputObject outputObject);

    void queryChatLogByType(InputObject inputObject, OutputObject outputObject);

    Map<String, List<ChatHistory>> queryLastChatHistory(List<String> uniqueIds);
}
