/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.label.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.label.entity.CrmLabel;
import com.skyeye.label.service.CrmLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CrmLabelController
 * @Description: CRM客户标签控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "CRM客户标签", tags = "CRM客户标签", modelName = "CRM客户标签")
public class CrmLabelController {

    @Autowired
    private CrmLabelService crmLabelService;

    @ApiOperation(id = "queryCrmLabelList", value = "查询CRM客户标签列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CrmLabelController/queryCrmLabelList")
    public void queryCrmLabelList(InputObject inputObject, OutputObject outputObject) {
        crmLabelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCrmLabel", value = "新增/编辑CRM客户标签", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CrmLabel.class)
    @RequestMapping("/post/CrmLabelController/writeCrmLabel")
    public void writeCrmLabel(InputObject inputObject, OutputObject outputObject) {
        crmLabelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCrmLabelById", value = "根据id查询CRM客户标签详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "客户标签id", required = "required")})
    @RequestMapping("/post/CrmLabelController/queryCrmLabelById")
    public void queryCrmLabelById(InputObject inputObject, OutputObject outputObject) {
        crmLabelService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCrmLabelByIds", value = "根据ids批量查询CRM客户标签详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "客户标签id集合", required = "required")})
    @RequestMapping("/post/CrmLabelController/queryCrmLabelByIds")
    public void queryCrmLabelByIds(InputObject inputObject, OutputObject outputObject) {
        crmLabelService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCrmLabelById", value = "根据ID删除CRM客户标签信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CrmLabelController/deleteCrmLabelById")
    public void deleteCrmLabelById(InputObject inputObject, OutputObject outputObject) {
        crmLabelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledLabelList", value = "获取所有启用的CRM客户标签列表", method = "GET", allUse = "0")
    @RequestMapping("/post/CrmLabelController/queryEnabledLabelList")
    public void queryEnabledLabelList(InputObject inputObject, OutputObject outputObject) {
        crmLabelService.queryEnabledLabelList(inputObject, outputObject);
    }

}

