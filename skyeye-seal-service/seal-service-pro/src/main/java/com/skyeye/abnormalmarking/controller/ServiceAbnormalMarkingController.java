/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.abnormalmarking.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.abnormalmarking.entity.ServiceAbnormalMarking;
import com.skyeye.abnormalmarking.service.ServiceAbnormalMarkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ServiceAbnormalMarkingController
 * @Description: 售后服务异常标记控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "售后服务异常标记", tags = "售后服务异常标记", modelName = "售后服务异常标记")
public class ServiceAbnormalMarkingController {

    @Autowired
    private ServiceAbnormalMarkingService serviceAbnormalMarkingService;

    @ApiOperation(id = "queryServiceAbnormalMarkingList", value = "查询售后服务异常标记列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ServiceAbnormalMarkingController/queryServiceAbnormalMarkingList")
    public void queryServiceAbnormalMarkingList(InputObject inputObject, OutputObject outputObject) {
        serviceAbnormalMarkingService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeServiceAbnormalMarking", value = "新增/编辑售后服务异常标记", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ServiceAbnormalMarking.class)
    @RequestMapping("/post/ServiceAbnormalMarkingController/writeServiceAbnormalMarking")
    public void writeServiceAbnormalMarking(InputObject inputObject, OutputObject outputObject) {
        serviceAbnormalMarkingService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServiceAbnormalMarkingById", value = "根据id查询售后服务异常标记详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "异常标记id", required = "required")})
    @RequestMapping("/post/ServiceAbnormalMarkingController/queryServiceAbnormalMarkingById")
    public void queryServiceAbnormalMarkingById(InputObject inputObject, OutputObject outputObject) {
        serviceAbnormalMarkingService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServiceAbnormalMarkingByIds", value = "根据ids批量查询售后服务异常标记详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "异常标记id集合", required = "required")})
    @RequestMapping("/post/ServiceAbnormalMarkingController/queryServiceAbnormalMarkingByIds")
    public void queryServiceAbnormalMarkingByIds(InputObject inputObject, OutputObject outputObject) {
        serviceAbnormalMarkingService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteServiceAbnormalMarkingById", value = "根据ID删除售后服务异常标记信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ServiceAbnormalMarkingController/deleteServiceAbnormalMarkingById")
    public void deleteServiceAbnormalMarkingById(InputObject inputObject, OutputObject outputObject) {
        serviceAbnormalMarkingService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledAbnormalMarkingList", value = "获取所有启用的售后服务异常标记列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ServiceAbnormalMarkingController/queryEnabledAbnormalMarkingList")
    public void queryEnabledAbnormalMarkingList(InputObject inputObject, OutputObject outputObject) {
        serviceAbnormalMarkingService.queryEnabledAbnormalMarkingList(inputObject, outputObject);
    }

}

