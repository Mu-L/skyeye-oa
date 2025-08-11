package com.skyeye.seal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.seal.entity.SalesExchanges;
import com.skyeye.seal.entity.SalesOutLet;
import com.skyeye.seal.service.SalesExchangesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "销售换货单", tags = "销售换货单", modelName = "销售模块")
public class SalesExchangesController {

    @Autowired
    private SalesExchangesService sealExchangesService;

    @ApiOperation(id = "querySalesExchangesList", value = "获取销售换货单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealExchangesController/querySalesExchangesList")
    public void querySalesReturnsList(InputObject inputObject, OutputObject outputObject) {
        sealExchangesService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSalesExchanges", value = "新增/编辑销售换货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesExchanges.class)
    @RequestMapping("/post/SealExchangesController/writeSalesExchanges")
    public void writeSalesReturns(InputObject inputObject, OutputObject outputObject) {
        sealExchangesService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesExchangesToDepotPutById", value = "转仓库入库单时，根据id查询销售换货信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealExchangesController/querySalesExchangesToDepotPutById")
    public void querySalesExchangesToDepotPutById(InputObject inputObject, OutputObject outputObject) {
        sealExchangesService.querySalesExchangesToDepotPutById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSalesExchangesToTurnDepot", value = "销售换货单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealExchangesController/insertSalesExchangesToTurnDepot")
    public void insertSalesExchangesToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        sealExchangesService.insertSalesExchangesToTurnDepot(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesExchangesToSalesOutLetById", value = "转销售出库单时，根据id查询销售换货信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealExchangesController/querySalesExchangesToSalesOutLetById")
    public void querySalesExchangesToSalesOutLetById(InputObject inputObject, OutputObject outputObject) {
        sealExchangesService.querySalesExchangesToSalesOutLetById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSalesExchangesToSalesOutLet", value = "销售换货单信息转销售出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SalesOutLet.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealExchangesController/insertSalesExchangesToSalesOutLet")
    public void insertSalesExchangesToSalesOutLet(InputObject inputObject, OutputObject outputObject) {
        sealExchangesService.insertSalesExchangesToSalesOutLet(inputObject, outputObject);
    }
}
