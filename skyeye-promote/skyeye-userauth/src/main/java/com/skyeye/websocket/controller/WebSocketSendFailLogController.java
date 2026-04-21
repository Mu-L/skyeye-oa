/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.websocket.service.WebSocketSendFailLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WebSocket 发送失败日志
 */
@RestController
@Api(value = "WebSocket发送失败日志", tags = "WebSocket发送失败日志", modelName = "WebSocket监控")
public class WebSocketSendFailLogController {

    @Autowired
    private WebSocketSendFailLogService webSocketSendFailLogService;

    @ApiOperation(id = "queryWebSocketSendFailLogPage", value = "分页查询WebSocket发送失败明细", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WebSocketSendFailLogController/queryWebSocketSendFailLogPage")
    public void queryWebSocketSendFailLogPage(InputObject inputObject, OutputObject outputObject) {
        webSocketSendFailLogService.queryPageList(inputObject, outputObject);
    }
}
