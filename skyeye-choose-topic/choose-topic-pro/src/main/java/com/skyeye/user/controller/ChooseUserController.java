/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.user.service.ChooseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ChooseUserController
 * @Description: 用户信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用户管理", tags = "用户管理", modelName = "用户管理")
public class ChooseUserController {

    @Autowired
    private ChooseUserService chooseUserService;

    @ApiOperation(id = "queryChooseUserList", value = "分页获取用户信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ChooseUserController/queryChooseUserList")
    public void queryUserList(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "chooseUserLogin", value = "用户登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "accountNumber", name = "accountNumber", value = "账号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "cId", name = "cId", value = "cId,用于手机端消息通知")})
    @RequestMapping("/post/ChooseUserController/chooseUserLogin")
    public void chooseUserLogin(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.chooseUserLogin(inputObject, outputObject);
    }

    @ApiOperation(id = "chooseUserExit", value = "注销登录", method = "POST", allUse = "2")
    @RequestMapping("/post/ChooseUserController/chooseUserExit")
    public void chooseUserExit(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.chooseUserExit(inputObject, outputObject);
    }

    @ApiOperation(id = "editChoosePassword", value = "修改密码", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "newPassword", name = "newPassword", value = "新密码", required = "required"),
        @ApiImplicitParam(id = "oldPassword", name = "oldPassword", value = "旧密码", required = "required")})
    @RequestMapping("/post/ChooseUserController/editChoosePassword")
    public void editChoosePassword(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.editChoosePassword(inputObject, outputObject);
    }

    @ApiOperation(id = "importChooseUser", value = "上传学生信息", method = "POST", allUse = "2")
    @RequestMapping("/post/ChooseUserController/importChooseUser")
    public void importChooseUser(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.importChooseUser(inputObject, outputObject);
    }

    @ApiOperation(id = "importTeacherChooseUser", value = "上传教师信息", method = "POST", allUse = "2")
    @RequestMapping("/post/ChooseUserController/importTeacherChooseUser")
    public void importTeacherChooseUser(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.importTeacherChooseUser(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteChooseUserById", value = "删除用户信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "用户id", required = "required")})
    @RequestMapping("/post/ChooseUserController/deleteChooseUserById")
    public void deleteChooseUserById(InputObject inputObject, OutputObject outputObject) {
        chooseUserService.deleteById(inputObject, outputObject);
    }
}