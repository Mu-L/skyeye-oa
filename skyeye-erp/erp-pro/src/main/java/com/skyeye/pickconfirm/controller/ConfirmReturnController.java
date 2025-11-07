/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pickconfirm.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.pickconfirm.entity.ConfirmReturn;
import com.skyeye.pickconfirm.service.ConfirmReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ConfirmReturnController
 * @Description: 物料退货单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/27 10:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "物料退货单", tags = "物料退货单", modelName = "物料确认")
public class ConfirmReturnController {

    @Autowired
    private ConfirmReturnService confirmReturnService;

    @ApiOperation(id = "queryConfirmReturnList", value = "获取物料退货单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ConfirmReturnController/queryConfirmReturnList")
    public void queryConfirmReturnList(InputObject inputObject, OutputObject outputObject) {
        confirmReturnService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeConfirmReturn", value = "新增/编辑物料退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ConfirmReturn.class)
    @RequestMapping("/post/ConfirmReturnController/writeConfirmReturn")
    public void writeConfirmReturn(InputObject inputObject, OutputObject outputObject) {
        confirmReturnService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryConfirmReturnTransById", value = "转仓库入库单时，根据id查询物料退货信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConfirmReturnController/queryConfirmReturnTransById")
    public void queryConfirmReturnTransById(InputObject inputObject, OutputObject outputObject) {
        confirmReturnService.queryConfirmReturnTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertConfirmReturnToTurnDepot", value = "物料退货单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConfirmReturnController/insertConfirmReturnToTurnDepot")
    public void insertConfirmReturnToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        confirmReturnService.insertConfirmReturnToTurnDepot(inputObject, outputObject);
    }

}
