/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chat.service;

import cn.hutool.json.JSONObject;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.chat.entity.TalkChatHistory;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: TalkChatHistoryService
 * @Description: 聊天记录服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/12 14:25
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TalkChatHistoryService extends SkyeyeBusinessService<TalkChatHistory> {

    String createEntity(JSONObject jsonObject, Integer chatType);

    String createEntity(JSONObject jsonObject, Integer chatType, Integer readType);

    void queryMyUnReadMessageList(InputObject inputObject, OutputObject outputObject);

    void editTalkChatHistoryToRead(InputObject inputObject, OutputObject outputObject);

    void queryMyTalkMessageList(InputObject inputObject, OutputObject outputObject);
}
