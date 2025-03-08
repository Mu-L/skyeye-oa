package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.ChatHistory;
import com.skyeye.school.chat.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "聊天历史管理", tags = "聊天历史管理", modelName = "聊天历史管理")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    /**
     * 新增好友聊天历史消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeChatHistory", value = "新增好友聊天历史消息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ChatHistory.class)
    @RequestMapping("/post/ChatHistoryController/writeChatHistory")
    public void writeChatHistory(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 查询好友聊天历史列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryChatHistory", value = "查询好友聊天历史列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ChatHistoryController/queryChatHistory")
    public void queryChatHistory(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.queryPageList(inputObject, outputObject);
    }

    /**
     * 查询好友聊天历史消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryChatHistoryByUniqueId", value = "查询好友聊天历史消息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "uniqueId", name = "uniqueId", value = "唯一标识id", required = "required")})
    @RequestMapping("/post/ChatHistoryController/queryChatHistoryByUniqueId")
    public void queryChatHistoryByUniqueId(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.queryChatHistoryByUniqueId(inputObject, outputObject);
    }

    /**
     * 删除全部好友聊天历史消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteChatHistoryByUniqueId", value = "删除全部好友聊天历史消息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "uniqueId", name = "uniqueId", value = "唯一标识id", required = "required")})
    @RequestMapping("/post/ChatHistoryController/deleteChatHistoryByUniqueId")
    public void deleteChatHistoryByUniqueId(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.deleteChatHistoryByUniqueId(inputObject, outputObject);
    }

    /**
     * 删除部分好友聊天历史消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteChatHistoryById", value = "删除部分好友聊天历史消息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ChatHistoryController/deleteChatHistoryById")
    public void deleteChatHistoryById(InputObject inputObject, OutputObject outputObject) {
        chatHistoryService.deleteChatHistoryById(inputObject, outputObject);
    }


}
