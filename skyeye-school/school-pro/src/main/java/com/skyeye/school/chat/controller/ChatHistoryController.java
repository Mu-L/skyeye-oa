package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "聊天历史管理", tags = "聊天历史管理", modelName = "聊天历史管理")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    @ApiOperation(id = "queryMyChatUnReadMessageList", value = "查询我的未读消息列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ChatHistoryController/queryMyChatUnReadMessageList")
    public void queryMyChatUnReadMessageList(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.queryMyChatUnReadMessageList(inputObject, outputObject);
    }

    @ApiOperation(id = "editChatHistoryToRead", value = "修改我与另一个用户/群聊的聊天记录为已读", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "sendId", name = "sendId", value = "发送人id", required = "required")})
    @RequestMapping("/post/ChatHistoryController/editChatHistoryToRead")
    public void editChatHistoryToRead(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.editChatHistoryToRead(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyChatMessageList", value = "查询我的最近的聊天消息列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ChatHistoryController/queryMyChatMessageList")
    public void queryMyChatMessageList(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.queryMyChatMessageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryChatLogByType", value = "获取聊天记录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "limit", name = "limit", value = "分页参数,每页多少条数据", required = "required,num"),
        @ApiImplicitParam(id = "page", name = "page", value = "分页参数,第几页", required = "required,num"),
        @ApiImplicitParam(id = "receiveId", name = "receiveId", value = "接收人id", required = "required")})
    @RequestMapping("/post/ChatHistoryController/queryChatLogByType")
    public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.queryChatLogByType(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMyChatMessageList", value = "删除我的聊天消息列表", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "uniqueId", name = "uniqueId", value = "唯一会话Id", required = "required")})
    @RequestMapping("/post/ChatHistoryController/deleteMyChatMessageList")
    public void deleteMyChatMessageList(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.deleteMyChatMessageList(inputObject, outputObject);
    }
}
