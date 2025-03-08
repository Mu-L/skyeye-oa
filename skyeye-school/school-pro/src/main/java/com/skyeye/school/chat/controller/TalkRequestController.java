package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.TalkRequest;
import com.skyeye.school.chat.service.TalkRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "好友申请管理", tags = "好友申请管理", modelName = "好友申请管理")
public class TalkRequestController {

    @Autowired
    private TalkRequestService talkMessageService;

    /**
     * 新增好友申请消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeTalkRequest", value = "新增好友申请消息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TalkRequest.class)
    @RequestMapping("/post/TalkMessageController/writeTalkRequest")
    public void writeTalkRequest(InputObject inputObject, OutputObject outputObject) {
        talkMessageService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 查询好友申请消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryTalkRequest", value = "查询好友申请消息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TalkMessageController/queryTalkRequest")
    public void queryTalkRequest(InputObject inputObject, OutputObject outputObject) {
        talkMessageService.queryTalkRequest(inputObject, outputObject);
    }

    /**
     * 查询好友自己的消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryTalkRequestFriend", value = "查询好友申请消息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/TalkMessageController/queryTalkRequestFriend")
    public void queryTalkRequestFriend(InputObject inputObject, OutputObject outputObject) {
        talkMessageService.queryTalkRequestFriend(inputObject, outputObject);
    }


    /**
     * 好友申请验证
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeFriendStatus", value = "好友申请验证", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
            @ApiImplicitParam(id = "status", name = "status", value = "申请状态", required = "required")})
    @RequestMapping("/post/TalkMessageController/changeFriendStatus")
    public void changeFriendStatus(InputObject inputObject, OutputObject outputObject) {
        talkMessageService.changeFriendStatus(inputObject, outputObject);
    }



}
