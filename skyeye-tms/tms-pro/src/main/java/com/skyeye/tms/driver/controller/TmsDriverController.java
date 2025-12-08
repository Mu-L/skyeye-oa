/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tms.driver.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tms.driver.entity.TmsDriver;
import com.skyeye.tms.driver.service.TmsDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TmsDriverController
 * @Description:司机管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/5 15:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "司机管理", tags = "司机管理", modelName = "司机管理")
public class TmsDriverController {

    @Autowired
    private TmsDriverService tmsDriverService;

    @ApiOperation(id = "queryTmsDriverList", value = "获取司机管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TmsDriverController/queryTmsDriverList")
    public void queryTmsDriverList(InputObject inputObject, OutputObject outputObject) {
        tmsDriverService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeTmsDriver", value = "新增/编辑司机管理", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = TmsDriver.class)
    @RequestMapping("/post/TmsDriverController/writeTmsDriver")
    public void writeTmsDriver(InputObject inputObject, OutputObject outputObject) {
        tmsDriverService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteTmsDriverById", value = "根据ID删除司机管理", method = "DELETE", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/TmsDriverController/deleteTmsDriverById")
    public void deleteTmsDriverById(InputObject inputObject, OutputObject outputObject) {
        tmsDriverService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledTmsDriverList", value = "获取已启用的司机", method = "GET", allUse = "2")
    @RequestMapping("/post/TmsDriverController/queryEnabledTmsDriverList")
    public void queryEnabledTmsDriverList(InputObject inputObject, OutputObject outputObject) {
        tmsDriverService.queryEnabledTmsDriverList(inputObject, outputObject);
    }

}




