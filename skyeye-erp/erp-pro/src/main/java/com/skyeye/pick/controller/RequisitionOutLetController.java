/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.pick.entity.RequisitionOutLet;
import com.skyeye.pick.service.RequisitionOutLetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RequisitionOutLetController
 * @Description: 领料出库单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 20:36
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "领料出库单", tags = "领料出库单", modelName = "物料单")
public class RequisitionOutLetController {

    @Autowired
    private RequisitionOutLetService requisitionOutLetService;

    @ApiOperation(id = "queryRequisitionOutLetList", value = "获取领料出库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/RequisitionOutLetController/queryRequisitionOutLetList")
    public void queryRequisitionOutLetList(InputObject inputObject, OutputObject outputObject) {
        requisitionOutLetService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeRequisitionOutLet", value = "新增/编辑领料出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = RequisitionOutLet.class)
    @RequestMapping("/post/RequisitionOutLetController/writeRequisitionOutLet")
    public void writeRequisitionOutLet(InputObject inputObject, OutputObject outputObject) {
        requisitionOutLetService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRequisitionOutLetsTransById", value = "转仓库出库单时，根据id查询领料出库单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RequisitionOutLetsController/queryRequisitionOutLetsTransById")
    public void queryRequisitionOutLetsTransById(InputObject inputObject, OutputObject outputObject) {
        requisitionOutLetService.queryRequisitionOutLetsTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertRequisitionOutLetsToTurnDepot", value = "领料出库单信息转仓库出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RequisitionOutLetsController/insertRequisitionOutLetsToTurnDepot")
    public void insertRequisitionOutLetsToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        requisitionOutLetService.insertRequisitionOutLetsToTurnDepot(inputObject, outputObject);
    }

}
