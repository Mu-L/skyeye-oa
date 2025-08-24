/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.member.entity.DocMember;
import com.skyeye.doc.member.service.DocMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocMemberController
 * @Description: 会员管理
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 22:23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "会员管理", tags = "会员管理", modelName = "会员管理")
public class DocMemberController {

    @Autowired
    private DocMemberService docMemberService;

    @ApiOperation(id = "queryDocMemberByList", value = "获取会员信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DocMemberController/queryMemberByList")
    public void queryMemberByList(InputObject inputObject, OutputObject outputObject) {
        docMemberService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDocMember", value = "添加/编辑会员信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DocMember.class)
    @RequestMapping("/post/DocMemberController/writeDocMember")
    public void writeDocMember(InputObject inputObject, OutputObject outputObject) {
        docMemberService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDocMemberById", value = "据ID查询会员信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DocMemberController/queryDocMemberById")
    public void queryDocMemberById(InputObject inputObject, OutputObject outputObject) {
        docMemberService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "loginDocMember", value = "登录", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "phone", name = "phone", value = "手机号", required = "required"),
        @ApiImplicitParam(id = "password", name = "password", value = "密码", required = "required")})
    @RequestMapping("/post/DocMemberController/loginDocMember")
    public void loginDocMember(InputObject inputObject, OutputObject outputObject) {
        docMemberService.loginDocMember(inputObject, outputObject);
    }

    @ApiOperation(id = "docMemberLoginMation", value = "从session中获取会员信息", method = "POST", allUse = "2")
    @RequestMapping("/post/DocMemberController/docMemberLoginMation")
    public void docMemberLoginMation(InputObject inputObject, OutputObject outputObject) {
        docMemberService.docMemberLoginMation(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentLoginMember", value = "获取当前登录会员信息", method = "GET", allUse = "2")
    @RequestMapping("/post/DocMemberController/queryCurrentLoginMember")
    public void queryCurrentLoginMember(InputObject inputObject, OutputObject outputObject) {
        docMemberService.queryCurrentLoginMember(inputObject, outputObject);
    }

    @ApiOperation(id = "logoutDocMember", value = "退出", method = "POST", allUse = "2")
    @RequestMapping("/post/DocMemberController/logoutDocMember")
    public void logoutDocMember(InputObject inputObject, OutputObject outputObject) {
        docMemberService.logoutDocMember(inputObject, outputObject);
    }

    @ApiOperation(id = "editDocMemberPassword", value = "修改密码", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "newPassword", name = "newPassword", value = "新密码", required = "required")})
    @RequestMapping("/post/DocMemberController/editDocMemberPassword")
    public void editDocMemberPassword(InputObject inputObject, OutputObject outputObject) {
        docMemberService.editDocMemberPassword(inputObject, outputObject);
    }

}
