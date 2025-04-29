/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.enumeration.SmsSceneEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ShopAppAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopAppAuthController
 * @Description: 商城登录管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/16 11:57
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商城登录管理", tags = "商城登录管理", modelName = "商城登录管理")
public class ShopAppAuthController {

    @Autowired
    private ShopAppAuthService shopAppAuthService;

    @ApiOperation(id = "shopLoginForPC", value = "登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "phone", name = "phone", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required")})
    @RequestMapping("/post/ShopAppAuthController/shopLoginForPC")
    public void shopLoginForPC(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.shopLoginForPC(inputObject, outputObject);
    }

    @ApiOperation(id = "shopLoginForApp", value = "手机端用户登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "phone", name = "phone", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "cId", name = "cId", value = "cId,用于手机端消息通知")})
    @RequestMapping("/post/ShopAppAuthController/shopLoginForApp")
    public void shopLoginForApp(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.shopLoginForApp(inputObject, outputObject);
    }

    @ApiOperation(id = "shopLogout", value = "退出", method = "POST", allUse = "2")
    @RequestMapping("/post/ShopAppAuthController/shopLogout")
    public void shopLogout(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.shopLogout(inputObject, outputObject);
    }

    @ApiOperation(id = "editShopUserPassword", value = "修改密码", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "newPassword", name = "newPassword", value = "新密码", required = "required")})
    @RequestMapping("/post/ShopAppAuthController/editShopUserPassword")
    public void editShopUserPassword(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.editShopUserPassword(inputObject, outputObject);
    }

    @ApiOperation(id = "editShopUserPasswordByPhone", value = "根据手机号修改密码", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "newPassword", name = "newPassword", value = "新密码", required = "required"),
        @ApiImplicitParam(id = "phone", name = "phone", value = "手机号", required = "required")})
    @RequestMapping("/post/ShopAppAuthController/editShopUserPasswordByPhone")
    public void editShopUserPasswordByPhone(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.editShopUserPasswordByPhone(inputObject, outputObject);
    }

    @ApiOperation(id = "sendShopSmsCode", value = "发送手机验证码", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "mobile", name = "mobile", value = "手机号"),
        @ApiImplicitParam(id = "scene", name = "scene", value = "发送场景", enumClass = SmsSceneEnum.class, required = "required")})
    @RequestMapping("/post/ShopAppAuthController/sendShopSmsCode")
    public void sendShopSmsCode(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.sendShopSmsCode(inputObject, outputObject);
    }

    @ApiOperation(id = "smsShopLogin", value = "短信验证码登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "mobile", name = "mobile", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "smsCode", name = "smsCode", value = "短信验证码", required = "required")})
    @RequestMapping("/post/ShopAppAuthController/smsLsmsShopLoginogin")
    public void smsShopLogin(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.smsShopLogin(inputObject, outputObject);
    }

    @ApiOperation(id = "smsShopMemberRegister", value = "会员注册", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "mobile", name = "mobile", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "smsCode", name = "smsCode", value = "短信验证码", required = "required")})
    @RequestMapping("/post/ShopAppAuthController/smsShopMemberRegister")
    public void smsShopMemberRegister(InputObject inputObject, OutputObject outputObject) {
        shopAppAuthService.smsShopMemberRegister(inputObject, outputObject);
    }

}
