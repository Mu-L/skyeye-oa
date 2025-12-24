/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.serviceitems.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.serviceitems.entity.ServiceItems;
import com.skyeye.serviceitems.service.ServiceItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ServiceItemsController
 * @Description: 售后服务项目控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "售后服务项目", tags = "售后服务项目", modelName = "售后服务项目")
public class ServiceItemsController {

    @Autowired
    private ServiceItemsService serviceItemsService;

    @ApiOperation(id = "queryServiceItemsList", value = "查询售后服务项目列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ServiceItemsController/queryServiceItemsList")
    public void queryServiceItemsList(InputObject inputObject, OutputObject outputObject) {
        serviceItemsService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeServiceItems", value = "新增/编辑售后服务项目", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ServiceItems.class)
    @RequestMapping("/post/ServiceItemsController/writeServiceItems")
    public void writeServiceItems(InputObject inputObject, OutputObject outputObject) {
        serviceItemsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServiceItemsById", value = "根据id查询售后服务项目详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "服务项目id", required = "required")})
    @RequestMapping("/post/ServiceItemsController/queryServiceItemsById")
    public void queryServiceItemsById(InputObject inputObject, OutputObject outputObject) {
        serviceItemsService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServiceItemsByIds", value = "根据ids批量查询售后服务项目详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "服务项目id集合", required = "required")})
    @RequestMapping("/post/ServiceItemsController/queryServiceItemsByIds")
    public void queryServiceItemsByIds(InputObject inputObject, OutputObject outputObject) {
        serviceItemsService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteServiceItemsById", value = "根据ID删除售后服务项目信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ServiceItemsController/deleteServiceItemsById")
    public void deleteServiceItemsById(InputObject inputObject, OutputObject outputObject) {
        serviceItemsService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledServiceItemsList", value = "获取所有启用的售后服务项目列表", method = "GET", allUse = "0")
    @RequestMapping("/post/ServiceItemsController/queryEnabledServiceItemsList")
    public void queryEnabledServiceItemsList(InputObject inputObject, OutputObject outputObject) {
        serviceItemsService.queryEnabledServiceItemsList(inputObject, outputObject);
    }

}

