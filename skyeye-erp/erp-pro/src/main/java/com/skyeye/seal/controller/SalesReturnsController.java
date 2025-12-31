/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.seal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.seal.entity.SalesReturns;
import com.skyeye.seal.service.SalesReturnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SalesReturnsController
 * @Description: 销售退货单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "销售退货单", tags = "销售退货单", modelName = "销售模块")
public class SalesReturnsController {

    @Autowired
    private SalesReturnsService salesReturnsService;

    @ApiOperation(id = "salesreturns001", value = "获取销售退货单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SalesReturnsController/querySalesReturnsList")
    public void querySalesReturnsList(InputObject inputObject, OutputObject outputObject) {
        salesReturnsService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSalesReturns", value = "新增/编辑销售退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesReturns.class)
    @RequestMapping("/post/SalesReturnsController/writeSalesReturns")
    public void writeSalesReturns(InputObject inputObject, OutputObject outputObject) {
        salesReturnsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesReturnsTransById", value = "转仓库入库单时，根据id查询销售退货信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesReturnsController/querySalesReturnsTransById")
    public void querySalesReturnsTransById(InputObject inputObject, OutputObject outputObject) {
        salesReturnsService.querySalesReturnsTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSalesReturnsToTurnDepot", value = "销售退货单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesReturnsController/insertSalesReturnsToTurnDepot")
    public void insertSalesReturnsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        salesReturnsService.insertSalesReturnsToTurnDepot(inputObject, outputObject);
    }

}
