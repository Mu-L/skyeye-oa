/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.service.FriendRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "好友管理", tags = "好友管理", modelName = "好友管理")
public class FriendRelationshipController {

    @Autowired
    private FriendRelationshipService friendRelationshipService;

    @ApiOperation(id = "queryFriendsList", value = "查询好友管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FriendRelationshipController/queryFriendsList")
    public void queryFriendsList(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.queryFriendsList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNoPageFriendsList", value = "不分页查询好友管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "所查询的Id", required = "required")})
    @RequestMapping("/post/FriendRelationshipController/queryNoPageFriendsList")
    public void queryNoPageFriendsList(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.queryNoPageFriendsList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFriendByUserId", value = "根据用户id查询好友消息，并判断是否是好友", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userId", name = "userId", value = "好友id", required = "required")})
    @RequestMapping("/post/FriendRelationshipController/queryFriendByUserId")
    public void queryFriendByUserId(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.queryFriendByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFriend", value = "删除好友消息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FriendRelationshipController/deleteFriend")
    public void deleteFriend(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.deleteById(inputObject, outputObject);
    }

}