/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.depot.service.DepotPutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DepotPutController
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 8:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "仓库入库单", tags = "仓库入库单", modelName = "仓库出入库")
public class DepotPutController {

    @Autowired
    private DepotPutService depotPutService;

    @ApiOperation(id = "queryDepotPutList", value = "获取仓库【入库管理】列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepotPutController/queryDepotPutList")
    public void queryDepotPutList(InputObject inputObject, OutputObject outputObject) {
        depotPutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDepotPutOrderList", value = "获取仓库入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
        @ApiImplicitParam(id = "type", name = "type", value = "类型", required = "required", defaultValue = "DepotPut")})
    @RequestMapping("/post/DepotPutController/queryDepotPutOrderList")
    public void queryDepotPutOrderList(InputObject inputObject, OutputObject outputObject) {
        depotPutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeDepotPut", value = "新增/编辑仓库入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DepotPut.class)
    @RequestMapping("/post/DepotPutController/writeDepotPut")
    public void writeDepotPut(InputObject inputObject, OutputObject outputObject) {
        depotPutService.saveOrUpdateEntity(inputObject, outputObject);
    }
}
