/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.otherwise.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.otherwise.entity.ErpOtherWiseOrder;
import com.skyeye.otherwise.service.OtherWiseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OtherWiseOrderController
 * @Description: 其他微服务订单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/20 11:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "其他微服务订单", tags = "其他微服务订单", modelName = "其他微服务订单")
public class OtherWiseOrderController {

    @Autowired
    private OtherWiseOrderService otherWiseOrderService;

    /**
     * 新增已经审批通过的单据
     * --给其他微服务使用，比如：售后服务等
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "createApprovelSuccessOrder", value = "新增已经审批通过的单据", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ErpOtherWiseOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "createId", name = "createId", value = "创建人id", required = "required"),
        @ApiImplicitParam(id = "createTime", name = "createTime", value = "创建时间", required = "required"),
        @ApiImplicitParam(id = "lastUpdateId", name = "lastUpdateId", value = "最后更新人id", required = "required"),
        @ApiImplicitParam(id = "lastUpdateTime", name = "lastUpdateTime", value = "最后更新日期", required = "required")})
    @RequestMapping("/post/OtherWiseOrderController/createApprovelSuccessOrder")
    public void createApprovelSuccessOrder(InputObject inputObject, OutputObject outputObject) {
        otherWiseOrderService.createApprovelSuccessOrder(inputObject, outputObject);
    }

    /**
     * 其他微服务的单据转仓库出库单时，根据id查询其他微服务的单据信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryOtherWiseOrderTransById", value = "其他微服务的单据转仓库出库单时，根据id查询其他微服务的单据信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OtherWiseOrderController/queryOtherWiseOrderTransById")
    public void queryOtherWiseOrderTransById(InputObject inputObject, OutputObject outputObject) {
        otherWiseOrderService.queryOtherWiseOrderTransById(inputObject, outputObject);
    }

    /**
     * 其他微服务的单据转仓库出库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertOtherWiseOrderToDepotOut", value = "其他微服务的单据转仓库出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OtherWiseOrderController/insertOtherWiseOrderToDepotOut")
    public void insertOtherWiseOrderToDepotOut(InputObject inputObject, OutputObject outputObject) {
        otherWiseOrderService.insertOtherWiseOrderToDepotOut(inputObject, outputObject);
    }

}
