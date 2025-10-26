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
import com.skyeye.type.entity.StoreType;
import com.skyeye.type.service.StoreTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StoreTypeController
 * @Description: 门店商品分类管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "门店商品分类管理", tags = "门店商品分类管理", modelName = "门店商品分类管理")
public class StoreTypeController {

    @Autowired
    private StoreTypeService storeTypeService;

    @ApiOperation(id = "writeStoreType", value = "新增/编辑门店商品分类信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = StoreType.class)
    @RequestMapping("/post/StoreTypeController/writeStoreType")
    public void writeStoreType(InputObject inputObject, OutputObject outputObject) {
        storeTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteStoreTypeByIds", value = "批量删除门店商品分类信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id列表，多个id用逗号分隔", required = "required")})
    @RequestMapping("/post/StoreTypeController/deleteStoreTypeByIds")
    public void deleteStoreTypeByIds(InputObject inputObject, OutputObject outputObject) {
        storeTypeService.deleteByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreTypeList", value = "获取门店商品分类信息列表", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id")})
    @RequestMapping("/post/StoreTypeController/queryStoreTypeList")
    public void queryStoreTypeList(InputObject inputObject, OutputObject outputObject) {
        storeTypeService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStoreTypePageList", value = "分页获取门店商品分类信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StoreTypeController/queryStoreTypePageList")
    public void queryStoreTypePageList(InputObject inputObject, OutputObject outputObject) {
        storeTypeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "selectStoreTypeById", value = "根据id获取门店商品分类信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/StoreTypeController/selectStoreTypeById")
    public void selectStoreTypeById(InputObject inputObject, OutputObject outputObject) {
        storeTypeService.selectById(inputObject, outputObject);
    }
}