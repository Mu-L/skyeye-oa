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
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.seal.entity.SalesOutLet;
import com.skyeye.seal.service.SalesOutLetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SalesOutLetController
 * @Description: 销售出库单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/26 17:27
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "销售出库单", tags = "销售出库单", modelName = "销售模块")
public class SalesOutLetController {

    @Autowired
    private SalesOutLetService salesOutLetService;

    @ApiOperation(id = "salesoutlet001", value = "获取销售出库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SalesOutLetController/querySalesOutLetList")
    public void querySalesOutLetList(InputObject inputObject, OutputObject outputObject) {
        salesOutLetService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSalesOutLet", value = "新增/编辑销售出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SalesOutLet.class)
    @RequestMapping("/post/SalesOutLetController/writeSalesOutLet")
    public void writeSalesOutLet(InputObject inputObject, OutputObject outputObject) {
        salesOutLetService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySalesOutLetTransById", value = "转仓库出库单时，根据id查询销售出库信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOutLetController/querySalesOutLetTransById")
    public void querySalesOutLetTransById(InputObject inputObject, OutputObject outputObject) {
        salesOutLetService.querySalesOutLetTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSalesOutLetToTurnDepot", value = "销售出库单信息转仓库出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SalesOutLetController/insertSalesOutLetToTurnDepot")
    public void insertSalesOutLetToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        salesOutLetService.insertSalesOutLetToTurnDepot(inputObject, outputObject);
    }

}
