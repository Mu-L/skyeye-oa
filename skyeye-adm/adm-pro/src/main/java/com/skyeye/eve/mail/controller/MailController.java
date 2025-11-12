/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.mail.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.mail.entity.Mail;
import com.skyeye.eve.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MailController
 * @Description: 通讯录管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/23 12:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "通讯录管理", tags = "通讯录管理", modelName = "通讯录管理")
public class MailController {

    @Autowired
    private MailService mailService;

    @ApiOperation(id = "queryMailList", value = "获取通讯录列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MailController/queryMailList")
    public void queryMailList(InputObject inputObject, OutputObject outputObject) {
        mailService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMail", value = "新增/编辑通讯录信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Mail.class)
    @RequestMapping("/post/MailController/writeMail")
    public void writeMail(InputObject inputObject, OutputObject outputObject) {
        mailService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMailById", value = "删除通讯录", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MailController/deleteMailById")
    public void deleteMailById(InputObject inputObject, OutputObject outputObject) {
        mailService.deleteById(inputObject, outputObject);
    }

}
