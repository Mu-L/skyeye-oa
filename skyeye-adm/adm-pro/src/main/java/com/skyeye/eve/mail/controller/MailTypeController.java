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
import com.skyeye.eve.mail.entity.MailType;
import com.skyeye.eve.mail.service.MailTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MailTypeController
 * @Description: 通讯录分组管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/23 12:55
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "通讯录分组管理", tags = "通讯录分组管理", modelName = "通讯录分组管理")
public class MailTypeController {

    @Autowired
    private MailTypeService mailTypeService;

    @ApiOperation(id = "queryMailTypeList", value = "获取通讯录类别列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MailTypeController/queryMailTypeList")
    public void queryMailTypeList(InputObject inputObject, OutputObject outputObject) {
        mailTypeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMailType", value = "新增/编辑通讯录类型信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = MailType.class)
    @RequestMapping("/post/MailTypeController/writeMailType")
    public void insertMailMationType(InputObject inputObject, OutputObject outputObject) {
        mailTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMailTypeById", value = "删除通讯录类型", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MailTypeController/deleteMailTypeById")
    public void deleteMailTypeById(InputObject inputObject, OutputObject outputObject) {
        mailTypeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllMailTypeList", value = "获取我的通讯录类型用作下拉框展示", method = "GET", allUse = "2")
    @RequestMapping("/post/MailTypeController/queryAllMailTypeList")
    public void queryAllMailTypeList(InputObject inputObject, OutputObject outputObject) {
        mailTypeService.queryAllMailTypeList(inputObject, outputObject);
    }

}
