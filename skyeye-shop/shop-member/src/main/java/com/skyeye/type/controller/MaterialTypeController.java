/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.type.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.type.entity.MaterialType;
import com.skyeye.type.service.MaterialTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MaterialTypeController
 * @Description: 商城商品分类控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/29 9:38
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "商城商品分类", tags = "商城商品分类", modelName = "商城商品分类")
public class MaterialTypeController {

    @Autowired
    private MaterialTypeService materialTypeService;

    @ApiOperation(id = "queryMaterialTypeList", value = "查询商城商品分类列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MaterialTypeController/queryMaterialTypeList")
    public void queryMaterialTypeList(InputObject inputObject, OutputObject outputObject) {
        materialTypeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeMaterialType", value = "新增/修改商城商品分类信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = MaterialType.class)
    @RequestMapping("/post/MaterialTypeController/writeMaterialType")
    public void writeMaterialType(InputObject inputObject, OutputObject outputObject) {
        materialTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteMaterialTypeById", value = "根据id删除商城商品分类信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MaterialTypeController/deleteMaterialTypeById")
    public void deleteMaterialTypeById(InputObject inputObject, OutputObject outputObject) {
        materialTypeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledMaterialTypeList", value = "获取所有启用的商城商品分类列表", method = "GET", allUse = "0")
    @RequestMapping("/post/MaterialTypeController/queryEnabledMaterialTypeList")
    public void queryAllMaterialTypeList(InputObject inputObject, OutputObject outputObject) {
        materialTypeService.queryEnabledMaterialTypeList(inputObject, outputObject);
    }
}
