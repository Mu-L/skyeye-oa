/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. all rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.handler.client;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.websocket.TalkWebSocket;

import java.util.Map;

/**
 * 策略：处理退出群聊消息（type=Twelfth），通知群创建人成员退出事件。
 */
public class TwelfthQuitGroupHandler implements TalkWebSocketClientMessageHandler {

    @Override
    public void handle(int type, JSONObject jsonObject, TalkWebSocket socket) {
        Map<String, Object> map1 = SocketConstants.sendOutGroupToCreaterMsg(jsonObject);
        CompanyTalkGroupService companyTalkGroupService = SpringUtils.getBean(CompanyTalkGroupService.class);
        CompanyTalkGroup groupMation = companyTalkGroupService.selectById(map1.get("groupId").toString());
        map1.put("toId", groupMation.getCreateId());
        socket.sendMessageTo(JSONUtil.toJsonStr(map1), groupMation.getCreateId(), null);
    }
}
