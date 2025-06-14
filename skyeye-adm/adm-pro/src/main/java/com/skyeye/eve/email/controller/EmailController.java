/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.email.entity.EmailParams;
import com.skyeye.eve.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EmailController
 * @Description: 邮件控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/9 9:06
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "邮件管理", tags = "邮件管理", modelName = "邮件管理")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @ApiOperation(id = "useremail004", value = "获取我的收件箱内容", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EmailController/queryInboxEmailListByEmailId")
    public void queryInboxEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        emailService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail007", value = "获取我的已发送邮件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EmailController/querySendedEmailListByEmailId")
    public void querySendedEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        emailService.querySendedEmailListByEmailId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail009", value = "获取我的已删除邮件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EmailController/queryDeleteEmailListByEmailId")
    public void queryDeleteEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        emailService.queryDeleteEmailListByEmailId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail011", value = "获取我的草稿箱邮件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EmailController/queryDraftsEmailListByEmailId")
    public void queryDraftsEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        emailService.queryDraftsEmailListByEmailId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail005", value = "根据id查询邮件信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EmailController/queryEmailById")
    public void queryEmailById(InputObject inputObject, OutputObject outputObject) {
        emailService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail012", value = "发送邮件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EmailParams.class)
    @RequestMapping("/post/EmailController/insertToSendEmailMationByUserId")
    public void insertToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        emailService.insertToSendEmailMationByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail013", value = "保存邮件为草稿", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EmailParams.class)
    @RequestMapping("/post/EmailController/insertToDraftsEmailMationByUserId")
    public void insertToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        emailService.insertToDraftsEmailMationByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail015", value = "草稿邮件修改", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EmailParams.class)
    @RequestMapping("/post/EmailController/editToDraftsEmailMationByUserId")
    public void editToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        emailService.editToDraftsEmailMationByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail016", value = "草稿箱里的邮件发送", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EmailParams.class)
    @RequestMapping("/post/EmailController/insertToSendEmailMationByEmailId")
    public void insertToSendEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) {
        emailService.insertToSendEmailMationByEmailId(inputObject, outputObject);
    }

    @ApiOperation(id = "useremail019", value = "转发邮件发送", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = EmailParams.class)
    @RequestMapping("/post/EmailController/insertForwardToSendEmailMationByUserId")
    public void insertForwardToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        emailService.insertForwardToSendEmailMationByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEmailById", value = "根据id删除邮件信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EmailController/deleteEmailById")
    public void deleteEmailById(InputObject inputObject, OutputObject outputObject) {
        emailService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "clearEmailByObjectId", value = "根据用户绑定的邮箱id清空邮件信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "用户绑定的邮箱id", required = "required")})
    @RequestMapping("/post/EmailController/clearEmailByObjectId")
    public void clearEmailByObjectId(InputObject inputObject, OutputObject outputObject) {
        emailService.clearEmailByObjectId(inputObject, outputObject);
    }

}
