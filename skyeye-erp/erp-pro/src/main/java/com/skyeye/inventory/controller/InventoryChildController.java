/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.inventory.service.InventoryChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: InventoryChildController
 * @Description: 盘点任务表-子单据控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 16:56
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "盘点任务表-子单据", tags = "盘点任务表-子单据", modelName = "盘点任务单")
public class InventoryChildController {

    @Autowired
    private InventoryChildService inventoryChildService;

    @ApiOperation(id = "queryInventoryChildList", value = "获取我的盘点任务信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InventoryChildController/queryInventoryChildList")
    public void queryInventoryChildList(InputObject inputObject, OutputObject outputObject) {
        inventoryChildService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInventoryChildById", value = "根据id查询盘点任务子单据信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InventoryChildController/queryInventoryChildById")
    public void queryInventoryChildById(InputObject inputObject, OutputObject outputObject) {
        inventoryChildService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "complateInventoryChild", value = "盘点完成", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "realNumber", name = "realNumber", value = "实际盘点数量(实盘后的数量)", required = "required,num"),
        @ApiImplicitParam(id = "profitNum", name = "profitNum", value = "盘盈数量", required = "required,num"),
        @ApiImplicitParam(id = "lossNum", name = "lossNum", value = "盘亏数量", required = "required,num"),
        @ApiImplicitParam(id = "profitNormsCode", name = "profitNormsCode", value = "盘盈明细的商品规格条形码编号"),
        @ApiImplicitParam(id = "lossNormsCode", name = "lossNormsCode", value = "盘亏明细的商品规格条形码编号")})
    @RequestMapping("/post/InventoryChildController/complateInventoryChild")
    public void complateInventoryChild(InputObject inputObject, OutputObject outputObject) {
        inventoryChildService.complateInventoryChild(inputObject, outputObject);
    }

}
