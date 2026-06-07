/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalFaq;
import com.skyeye.portal.service.PortalFaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 官网常见问题接口
 */
@RestController
@Api(value = "官网常见问题", tags = "官网常见问题", modelName = "门户管理")
public class PortalFaqController {

    @Autowired
    private PortalFaqService portalFaqService;

    @ApiOperation(id = "queryPortalFaqList", value = "分页查询常见问题", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PortalFaqController/queryPortalFaqList")
    public void queryPortalFaqList(InputObject inputObject, OutputObject outputObject) {
        portalFaqService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePortalFaq", value = "新增/编辑常见问题", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PortalFaq.class)
    @RequestMapping("/post/PortalFaqController/writePortalFaq")
    public void writePortalFaq(InputObject inputObject, OutputObject outputObject) {
        portalFaqService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePortalFaqById", value = "删除常见问题", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PortalFaqController/deletePortalFaqById")
    public void deletePortalFaqById(InputObject inputObject, OutputObject outputObject) {
        portalFaqService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalFaqById", value = "根据id获取常见问题", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PortalFaqController/queryPortalFaqById")
    public void queryPortalFaqById(InputObject inputObject, OutputObject outputObject) {
        portalFaqService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledPortalFaqList", value = "获取已启用常见问题（官网展示）", method = "POST", allUse = "0")
    @RequestMapping("/post/PortalFaqController/queryEnabledPortalFaqList")
    public void queryEnabledPortalFaqList(InputObject inputObject, OutputObject outputObject) {
        portalFaqService.queryEnabledPortalFaqList(inputObject, outputObject);
    }
}
