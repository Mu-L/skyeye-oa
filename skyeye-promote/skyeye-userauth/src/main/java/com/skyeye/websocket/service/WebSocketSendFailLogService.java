/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.websocket.entity.WebSocketSendFailLog;

public interface WebSocketSendFailLogService extends SkyeyeBusinessService<WebSocketSendFailLog> {

    /**
     * 记录WebSocket发送失败日志（最终失败）。
     */
    void saveSendFailLog(String targetUserId, String sessionId, String sendStage, String messageContent, String errorMessage, String nodeId, String tenantId);
}

