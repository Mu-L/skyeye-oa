package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.constans.WebSocketConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.service.ChatWebSocketMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "chatwebsocket消息处理", tags = "chatwebsocket消息处理", modelName = "chatwebsocket消息处理")
public class ChatWebSocketMsgController {

    @Autowired
    private ChatWebSocketMsgService chatWebSocketMsgService;

    @ApiOperation(id = "sendChatWebSocketMsgToUser", value = "发送chatwebsocket消息给指定用户", method = "POST", allUse = "0")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "userIdList", name = "userIdList", value = "用户id，集合形式", required = "required,json"),
            @ApiImplicitParam(id = "msg", name = "msg", value = "消息内容", required = "required"),
            @ApiImplicitParam(id = "messageType", name = "messageType", value = "消息类型", required = "required", enumClass = WebSocketConstants.MessageType.class, defaultValue = "5")})
    @RequestMapping("/post/ChatWebSocketMsgController/sendChatWebSocketMsgToUser")
    public void sendChatWebSocketMsgToUser(InputObject inputObject, OutputObject outputObject) {
        chatWebSocketMsgService.sendChatWebSocketMsgToUser(inputObject, outputObject);
    }

    @ApiOperation(id = "sendChatWebSocketMsgToAll", value = "发送chatwebsocket消息给所有用户", method = "POST", allUse = "0")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "msg", name = "msg", value = "消息内容", required = "required"),
            @ApiImplicitParam(id = "messageType", name = "messageType", value = "消息类型", required = "required", enumClass = WebSocketConstants.MessageType.class, defaultValue = "5")})
    @RequestMapping("/post/ChatWebSocketMsgController/sendChatWebSocketMsgToAll")
    public void sendChatWebSocketMsgToAll(InputObject inputObject, OutputObject outputObject) {
        chatWebSocketMsgService.sendChatWebSocketMsgToAll(inputObject, outputObject);
    }

}
