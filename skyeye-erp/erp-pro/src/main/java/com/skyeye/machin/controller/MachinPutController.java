/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.machin.entity.MachinPut;
import com.skyeye.machin.service.MachinPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MachinPutController
 * @Description: 加工入库单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/6 22:03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "加工入库单", tags = "加工入库单", modelName = "加工单管理")
public class MachinPutController {

    @Autowired
    private MachinPutService machinPutService;

    @ApiOperation(id = "queryMachinPutList", value = "获取加工入库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MachinPutController/queryMachinPutList")
    public void queryMachinPutList(InputObject inputObject, OutputObject outputObject) {
        machinPutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMachinPut", value = "新增/编辑加工入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = MachinPut.class)
    @RequestMapping("/post/MachinPutController/writeMachinPut")
    public void writeMachinPut(InputObject inputObject, OutputObject outputObject) {
        machinPutService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMachinPutTransById", value = "转仓库入库单时，根据id查询加工入库单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinPutController/queryMachinPutTransById")
    public void queryMachinPutTransById(InputObject inputObject, OutputObject outputObject) {
        machinPutService.queryMachinPutTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertMachinPutToTurnDepot", value = "加工入库单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinPutController/insertMachinPutToTurnDepot")
    public void insertMachinPutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        machinPutService.insertMachinPutToTurnDepot(inputObject, outputObject);
    }

}
