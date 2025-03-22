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
import com.skyeye.user.entity.User;
import com.skyeye.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserController
 * @Description: 用户信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用户管理", tags = "用户管理", modelName = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页获取用户列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserList", value = "分页获取用户信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/UserController/queryUserList")
    public void queryUserList(InputObject inputObject, OutputObject outputObject) {
        userService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据id查询用户信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserById", value = "根据ID查询用户信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/UserController/queryUserById")
    public void queryUserById(InputObject inputObject, OutputObject outputObject) {
        userService.selectById(inputObject, outputObject);
    }

    /**
     * 根据ID批量查询用户信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserByIds", value = "根据ID批量查询用户信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/UserController/queryUserByIds")
    public void queryUserByIds(InputObject inputObject, OutputObject outputObject) {
        userService.selectByIds(inputObject, outputObject);
    }

    /**
     * 根据姓名或者学号获取用户信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryUserByRealNameOrStudentNumber", value = "根据姓名或者学号获取用户信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/UserController/queryUserByRealNameOrStudentNumber")
    public void queryUserByRealNameOrStudentNumber(InputObject inputObject, OutputObject outputObject) {
        userService.queryUserByRealNameOrStudentNumber(inputObject, outputObject);
    }

    /**
     * 更新用户信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "updateUserById", value = "编辑用户信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = User.class)
    @RequestMapping("/post/UserController/updateUserById")
    public void updateUserById(InputObject inputObject, OutputObject outputObject) {
        userService.updateEntity(inputObject, outputObject);
    }

    /**
     * 用户登录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wallUserLogin", value = "用户登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "accountNumber", name = "accountNumber", value = "账号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "cId", name = "cId", value = "cId,用于手机端消息通知")})
    @RequestMapping("/post/UserController/wallUserLogin")
    public void wallUserLogin(InputObject inputObject, OutputObject outputObject) {
        userService.wallUserLogin(inputObject, outputObject);
    }

    /**
     * 用户注册
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wallUserRegister", value = "用户注册", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "姓名", required = "required"),
        @ApiImplicitParam(id = "accountNumber", name = "accountNumber", value = "账号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required")})
    @RequestMapping("/post/UserController/wallUserRegister")
    public void wallUserRegister(InputObject inputObject, OutputObject outputObject) {
        userService.wallUserRegister(inputObject, outputObject);
    }

    /**
     * 注销登录
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wallUserExit", value = "注销登录", method = "POST", allUse = "2")
    @RequestMapping("/post/UserController/wallUserExit")
    public void wallUserExit(InputObject inputObject, OutputObject outputObject) {
        userService.wallUserExit(inputObject, outputObject);
    }

    /**
     * 修改密码
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "editWallPassword", value = "修改密码", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "newPassword", name = "newPassword", value = "新密码", required = "required"),
        @ApiImplicitParam(id = "oldPassword", name = "oldPassword", value = "旧密码", required = "required")})
    @RequestMapping("/post/UserController/editWallPassword")
    public void editWallPassword(InputObject inputObject, OutputObject outputObject) {
        userService.editWallPassword(inputObject, outputObject);
    }

    /**
     * 老师用户进入主页
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryTeacherUserById", value = "老师用户进入主页", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "用户id", required = "required")})
    @RequestMapping("/post/UserController/queryTeacherUserById")
    public void queryTeacherUserById(InputObject inputObject, OutputObject outputObject) {
        userService.queryTeacherUserById(inputObject, outputObject);
    }
}