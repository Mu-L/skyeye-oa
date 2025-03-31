package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.service.UniqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "聊天会话管理", tags = "聊天会话管理", modelName = "聊天会话管理")
public class UniqueController {

    @Autowired
    private UniqueService uniqueService;

    @ApiOperation(id = "queryMyUniqueList", value = "查询我的最近的聊天消息列表", method = "GET", allUse = "2")
    @RequestMapping("/post/UniqueController/queryMyUniqueList")
    public void queryMyUniqueList(InputObject inputObject, OutputObject outputObject) {
        uniqueService.queryMyChatMessageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMyChatUniqueList", value = "删除我的聊天消息列表", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "uniqueId", name = "uniqueId", value = "唯一会话Id", required = "required")})
    @RequestMapping("/post/UniqueController/deleteMyChatUniqueList")
    public void deleteMyChatUniqueList(InputObject inputObject, OutputObject outputObject) {
        uniqueService.deleteMyChatUniqueList(inputObject, outputObject);
    }

}
