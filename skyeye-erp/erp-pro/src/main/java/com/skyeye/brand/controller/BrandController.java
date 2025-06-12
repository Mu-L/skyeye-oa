/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.brand.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.brand.entity.Brand;
import com.skyeye.brand.service.BrandService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: BrandController
 * @Description: 品牌管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/17 21:16
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "品牌管理", tags = "品牌管理", modelName = "品牌管理")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @ApiOperation(id = "queryBrandList", value = "获取品牌列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BrandController/queryBrandList")
    public void queryBrandList(InputObject inputObject, OutputObject outputObject) {
        brandService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledBrandList", value = "根据已启用查询品牌列表", method = "POST", allUse = "0")
    @RequestMapping("/post/BrandController/queryEnabledBrandList")
    public void queryEnabledBrandList(InputObject inputObject, OutputObject outputObject) {
        brandService.queryEnabledBrandList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPageEnabledBrandList", value = "根据已启用查询品牌列表(可分页)", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BrandController/queryPageEnabledBrandList")
    public void queryPageEnabledBrandList(InputObject inputObject, OutputObject outputObject) {
        brandService.queryPageEnabledBrandList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeBrand", value = "新增/编辑品牌信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Brand.class)
    @RequestMapping("/post/BrandController/writeBrand")
    public void writeBrand(InputObject inputObject, OutputObject outputObject) {
        brandService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteBrandById", value = "根据ID删除品牌信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/BrandController/deleteBrandById")
    public void deleteBrandById(InputObject inputObject, OutputObject outputObject) {
        brandService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrandById", value = "根据ID查询品牌信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/BrandController/queryBrandById")
    public void queryBrandById(InputObject inputObject, OutputObject outputObject) {
        brandService.selectById(inputObject, outputObject);
    }

}
