/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.supplier.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.supplier.entity.Supplier;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SupplierController
 * @Description: 供应商管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/14 10:48
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "供应商管理", tags = "供应商管理", modelName = "供应商管理")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @ApiOperation(id = "supplier001", value = "获取供应商列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SupplierController/querySupplierList")
    public void querySupplierList(InputObject inputObject, OutputObject outputObject) {
        supplierService.querySupplierList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSupplier", value = "添加/编辑供应商", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Supplier.class)
    @RequestMapping("/post/SupplierContronller/writeSupplier")
    public void writeSupplier(InputObject inputObject, OutputObject outputObject) {
        supplierService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySupplierListByIds", value = "根据id批量查询供应商信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierController/querySupplierListByIds")
    public void querySupplierListByIds(InputObject inputObject, OutputObject outputObject) {
        supplierService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "supplier004", value = "删除供应商信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierController/deleteSupplierById")
    public void deleteSupplierById(InputObject inputObject, OutputObject outputObject) {
        supplierService.deleteById(inputObject, outputObject);
    }

}
