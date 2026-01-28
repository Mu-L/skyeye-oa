/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.enterprise.entity.EnterpriseProduct;
import com.skyeye.enterprise.service.EnterpriseProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: EnterpriseProductController
 * @Description: 企业商品管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "企业商品管理", tags = "企业商品管理", modelName = "企业商品管理")
public class EnterpriseProductController {

    @Autowired
    private EnterpriseProductService enterpriseProductService;

    @ApiOperation(id = "queryEnterpriseProductList", value = "获取企业商品信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EnterpriseProductController/queryEnterpriseProductList")
    public void queryEnterpriseProductList(InputObject inputObject, OutputObject outputObject) {
        enterpriseProductService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeEnterpriseProduct", value = "新增/编辑企业商品信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = EnterpriseProduct.class)
    @RequestMapping("/post/EnterpriseProductController/writeEnterpriseProduct")
    public void writeEnterpriseProduct(InputObject inputObject, OutputObject outputObject) {
        enterpriseProductService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteEnterpriseProductById", value = "删除企业商品信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EnterpriseProductController/deleteEnterpriseProductById")
    public void deleteEnterpriseProductById(InputObject inputObject, OutputObject outputObject) {
        enterpriseProductService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnterpriseProductById", value = "根据id获取企业商品信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/EnterpriseProductController/queryEnterpriseProductById")
    public void queryEnterpriseProductById(InputObject inputObject, OutputObject outputObject) {
        enterpriseProductService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEnterpriseProductList", value = "获取所有企业商品列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/EnterpriseProductController/queryAllEnterpriseProductList")
    public void queryAllEnterpriseProductList(InputObject inputObject, OutputObject outputObject) {
        enterpriseProductService.queryAllEnterpriseProductList(inputObject, outputObject);
    }

}