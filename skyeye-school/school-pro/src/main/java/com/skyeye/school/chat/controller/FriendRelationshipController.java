package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.service.FriendRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "好友管理", tags = "好友管理", modelName = "好友管理")
public class FriendRelationshipController {

    @Autowired
    private FriendRelationshipService friendRelationshipService;

    /**
     * 查询好友管理列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFriendsList", value = "查询好友管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FriendRelationshipController/queryFriendsList")
    public void queryFriendsList(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.queryPageList(inputObject, outputObject);
    }

    /**
     * 查询好友消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFriend", value = "查询好友消息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FriendRelationshipController/queryFriend")
    public void queryFriend(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.selectById(inputObject, outputObject);
    }

    /**
     * 删除好友消息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteFriend", value = "删除好友消息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FriendRelationshipController/deleteFriend")
    public void deleteFriend(InputObject inputObject, OutputObject outputObject) {
        friendRelationshipService.deleteById(inputObject, outputObject);
    }

}