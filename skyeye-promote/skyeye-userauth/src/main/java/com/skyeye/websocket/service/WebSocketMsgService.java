/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: WebSocketMsgService
 * @Description: websocket消息服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/11 20:22
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WebSocketMsgService {

    void sendWebSocketMsgToUser(InputObject inputObject, OutputObject outputObject);

    void sendWebSocketMsgToAll(InputObject inputObject, OutputObject outputObject);

    void sendWebSocketPointMsgToUser(InputObject inputObject, OutputObject outputObject);
}
