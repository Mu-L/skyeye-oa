/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.enterprise.entity.UserEnterprise;
import com.skyeye.enterprise.enums.UserEnterpriseType;
import com.skyeye.enterprise.service.UserEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UserEnterpriseController
 * @Description: 企业账户管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/15 14:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "企业账户", tags = "企业账户", modelName = "企业账户")
public class UserEnterpriseController {

    @Autowired
    private UserEnterpriseService userEnterpriseService;

    @ApiOperation(id = "createUserEnterprise", value = "创建企业账户", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = UserEnterprise.class)
    @RequestMapping("/post/UserEnterpriseController/createUserEnterprise")
    public void createUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "updateUserEnterprise", value = "编辑企业账户", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "companyName", name = "companyName", value = "企业名称", required = "required"),
        @ApiImplicitParam(id = "socialCreditCode", name = "socialCreditCode", value = "营业执照注册号", required = "required"),
        @ApiImplicitParam(id = "businessLicenseLogo", name = "businessLicenseLogo", value = "营业执照图片路径", required = "required"),
        @ApiImplicitParam(id = "type", name = "type", value = "类型", required = "required", enumClass = UserEnterpriseType.class),
        @ApiImplicitParam(id = "name", name = "name", value = "管理员姓名", required = "required"),
        @ApiImplicitParam(id = "idCard", name = "idCard", value = "管理员身份证", required = "required,idcard"),
        @ApiImplicitParam(id = "idCardFrontLogo", name = "idCardFrontLogo", value = "身份证正面照", required = "required"),
        @ApiImplicitParam(id = "idCardBackLogo", name = "idCardBackLogo", value = "身份证反面照", required = "required"),
        @ApiImplicitParam(id = "phone", name = "phone", value = "管理员手机号", required = "required,phone")})
    @RequestMapping("/post/UserEnterpriseController/updateUserEnterprise")
    public void updateUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.updateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "updateUserEnterprisePassword", value = "修改密码", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required")})
    @RequestMapping("/post/UserEnterpriseController/updateUserEnterprisePassword")
    public void updateUserEnterprisePassword(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.updateUserEnterprisePassword(inputObject, outputObject);
    }

    @ApiOperation(id = "loginUserEnterprise", value = "登录，适配PC端和移动端", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "userCode", name = "userCode", value = "用户名", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required"),
        @ApiImplicitParam(id = "requestType", name = "requestType", value = "请求类型，也用来标识登录的终端", required = "required", enumClass = RequestType.class)})
    @RequestMapping("/post/UserEnterpriseController/loginUserEnterprise")
    public void loginUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.loginUserEnterprise(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentLoginUserEnterprise", value = "获取当前登录的企业账户信息", method = "POST", allUse = "2")
    @RequestMapping("/post/UserEnterpriseController/queryCurrentLoginUserEnterprise")
    public void queryCurrentLoginUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.queryCurrentLoginUserEnterprise(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUserEnterpriseById", value = "根据id获取企业账户信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/UserEnterpriseController/queryUserEnterpriseById")
    public void queryUserEnterpriseById(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUserEnterpriseList", value = "查询企业账户列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/UserEnterpriseController/queryUserEnterpriseList")
    public void queryUserEnterpriseList(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "existUserEnterprise", value = "企业账户退出", method = "POST", allUse = "2")
    @RequestMapping("/post/UserEnterpriseController/existUserEnterprise")
    public void existUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        userEnterpriseService.existUserEnterprise(inputObject, outputObject);
    }

}
