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
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.pick.entity.ReturnPut;
import com.skyeye.pick.service.ReturnPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReturnPutController
 * @Description: 退料入库单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 21:06
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "退料入库单", tags = "退料入库单", modelName = "物料单")
public class ReturnPutController {

    @Autowired
    private ReturnPutService returnPutService;

    @ApiOperation(id = "queryReturnPutList", value = "获取退料入库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReturnPutController/queryReturnPutList")
    public void queryReturnPutList(InputObject inputObject, OutputObject outputObject) {
        returnPutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeReturnPut", value = "新增/编辑退料入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ReturnPut.class)
    @RequestMapping("/post/ReturnPutController/writeReturnPut")
    public void writeReturnPut(InputObject inputObject, OutputObject outputObject) {
        returnPutService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReturnPutTransById", value = "转仓库入库单时，根据id查询退料入库信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReturnPutController/queryReturnPutTransById")
    public void queryReturnPutTransById(InputObject inputObject, OutputObject outputObject) {
        returnPutService.queryReturnPutTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertReturnPutToTurnDepot", value = "退料入库单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReturnPutController/insertReturnPutToTurnDepot")
    public void insertReturnPutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        returnPutService.insertReturnPutToTurnDepot(inputObject, outputObject);
    }

}
