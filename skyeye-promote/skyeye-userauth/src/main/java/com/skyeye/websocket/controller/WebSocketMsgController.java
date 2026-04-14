/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.websocket.service.WebSocketMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WebSocketMsgController
 * @Description: websocket消息处理
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/11 20:21
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "websocket消息处理", tags = "websocket消息处理", modelName = "websocket消息处理")
public class WebSocketMsgController {

    @Autowired
    private WebSocketMsgService webSocketMsgService;

    @ApiOperation(id = "sendWebSocketMsgToUser", value = "发送websocket消息给指定用户", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userIdList", name = "userIdList", value = "用户id，集合形式", required = "required,json"),
        @ApiImplicitParam(id = "msg", name = "msg", value = "消息内容", required = "required"),
        @ApiImplicitParam(id = "messageType", name = "messageType", value = "消息类型", required = "required", enumClass = SocketConstants.MessageType.class, defaultValue = "5")})
    @RequestMapping("/post/WebSocketMsgController/sendWebSocketMsgToUser")
    public void sendWebSocketMsgToUser(InputObject inputObject, OutputObject outputObject) {
        webSocketMsgService.sendWebSocketMsgToUser(inputObject, outputObject);
    }

    @ApiOperation(id = "sendWebSocketMsgToAll", value = "发送websocket消息给所有用户", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "msg", name = "msg", value = "消息内容", required = "required"),
        @ApiImplicitParam(id = "messageType", name = "messageType", value = "消息类型", required = "required", enumClass = SocketConstants.MessageType.class, defaultValue = "5")})
    @RequestMapping("/post/WebSocketMsgController/sendWebSocketMsgToAll")
    public void sendWebSocketMsgToAll(InputObject inputObject, OutputObject outputObject) {
        webSocketMsgService.sendWebSocketMsgToAll(inputObject, outputObject);
    }

    @ApiOperation(id = "sendWebSocketPointMsgToUser", value = "分别发送不同的websocket消息给指定用户", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userMsgList", name = "userMsgList", value = "用户id和消息内容的集合，集合形式，包括userId、msg和objectData三个属性", required = "required,json"),
        @ApiImplicitParam(id = "messageType", name = "messageType", value = "消息类型", required = "required", enumClass = SocketConstants.MessageType.class, defaultValue = "5")})
    @RequestMapping("/post/WebSocketMsgController/sendWebSocketPointMsgToUser")
    public void sendWebSocketPointMsgToUser(InputObject inputObject, OutputObject outputObject) {
        webSocketMsgService.sendWebSocketPointMsgToUser(inputObject, outputObject);
    }

    @ApiOperation(id = "queryWebSocketRuntimeMetrics", value = "查询WebSocket运行指标", method = "POST", allUse = "2")
    @RequestMapping("/post/WebSocketMsgController/queryWebSocketRuntimeMetrics")
    public void queryWebSocketRuntimeMetrics(InputObject inputObject, OutputObject outputObject) {
        webSocketMsgService.queryWebSocketRuntimeMetrics(inputObject, outputObject);
    }

}
