/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.Depot;
import com.skyeye.depot.service.ErpDepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ErpDepotController
 * @Description: 仓库管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2019/9/14 10:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "仓库管理", tags = "仓库管理", modelName = "仓库管理")
public class ErpDepotController {

    @Autowired
    private ErpDepotService erpDepotService;

    @ApiOperation(id = "storehouse001", value = "获取仓库信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ErpDepotController/queryStoreHouseList")
    public void queryStoreHouseList(InputObject inputObject, OutputObject outputObject) {
        erpDepotService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDepotMation", value = "新增/编辑仓库信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Depot.class)
    @RequestMapping("/post/ErpDepotController/writeDepotMation")
    public void writeDepotMation(InputObject inputObject, OutputObject outputObject) {
        erpDepotService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDepotByIds", value = "根据id批量获取仓库信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/ErpDepotController/queryDepotByIds")
    public void queryDepotByIds(InputObject inputObject, OutputObject outputObject) {
        erpDepotService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "storehouse004", value = "删除仓库信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ErpDepotController/deleteStoreHouseById")
    public void deleteStoreHouseById(InputObject inputObject, OutputObject outputObject) {
        erpDepotService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllStoreHouseList", value = "获取所有仓库", method = "GET", allUse = "2")
    @RequestMapping("/post/ErpDepotController/queryAllStoreHouseList")
    public void queryAllStoreHouseList(InputObject inputObject, OutputObject outputObject) {
        erpDepotService.queryAllStoreHouseList(inputObject, outputObject);
    }

    @ApiOperation(id = "storehouse009", value = "获取当前登录用户管理的仓库列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ErpDepotController/queryStoreHouseListByCurrentUserId")
    public void queryStoreHouseListByCurrentUserId(InputObject inputObject, OutputObject outputObject) {
        erpDepotService.queryStoreHouseListByCurrentUserId(inputObject, outputObject);
    }

}
