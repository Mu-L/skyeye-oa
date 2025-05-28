/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StatisticsController
 * @Description: 统计模块
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/20 22:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "统计模块", tags = "统计模块", modelName = "统计模块")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation(id = "statistics001", value = "入库明细", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StatisticsController/queryWarehousingDetails")
    public void queryWarehousingDetails(InputObject inputObject, OutputObject outputObject) {
        statisticsService.queryWarehousingDetails(inputObject, outputObject);
    }

    @ApiOperation(id = "statistics002", value = "出库明细", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StatisticsController/queryOutgoingDetails")
    public void queryOutgoingDetails(InputObject inputObject, OutputObject outputObject) {
        statisticsService.queryOutgoingDetails(inputObject, outputObject);
    }

    @ApiOperation(id = "statistics003", value = "进货统计", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StatisticsController/queryInComimgDetails")
    public void queryInComimgDetails(InputObject inputObject, OutputObject outputObject) {
        statisticsService.queryInComimgDetails(inputObject, outputObject);
    }

    @ApiOperation(id = "statistics004", value = "销售统计", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StatisticsController/querySalesDetails")
    public void querySalesDetails(InputObject inputObject, OutputObject outputObject) {
        statisticsService.querySalesDetails(inputObject, outputObject);
    }

    @ApiOperation(id = "statistics005", value = "客户对账", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StatisticsController/queryCustomerReconciliationDetails")
    public void queryCustomerReconciliationDetails(InputObject inputObject, OutputObject outputObject) {
        statisticsService.queryCustomerReconciliationDetails(inputObject, outputObject);
    }

    @ApiOperation(id = "statistics006", value = "供应商对账", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StatisticsController/querySupplierReconciliationDetails")
    public void querySupplierReconciliationDetails(InputObject inputObject, OutputObject outputObject) {
        statisticsService.querySupplierReconciliationDetails(inputObject, outputObject);
    }

}
