/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pick.service.DepartmentStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DepartmentStockController
 * @Description: 物料单信息管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/20 10:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "部门/车间物料库存信息", tags = "部门/车间物料库存信息", modelName = "物料单")
public class DepartmentStockController {

    @Autowired
    private DepartmentStockService departmentStockService;

    @ApiOperation(id = "erpdepartstock001", value = "获取登录人部门/车间物料库存信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DepartmentStockController/queryDepartmentStockList")
    public void queryDepartmentStockList(InputObject inputObject, OutputObject outputObject) {
        departmentStockService.queryDepartmentStockList(inputObject, outputObject);
    }

}
