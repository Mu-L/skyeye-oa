/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.shopmaterial.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.shopmaterial.service.ShopMaterialStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopMaterialStoreController
 * @Description: 商城商品上线的门店控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/18 14:32
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商城商品上线的门店", tags = "商城商品上线的门店", modelName = "商城商品上线的门店")
public class ShopMaterialStoreController {

    @Autowired
    private ShopMaterialStoreService shopMaterialStoreService;

    @ApiOperation(id = "saveShopMaterialStore", value = "新增门店时，将所有商品同步到该门店", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/saveShopMaterialStore")
    public void saveShopMaterialStore(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.saveShopMaterialStore(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialById", value = "根据id获取商城商品信息", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialById")
    public void queryShopMaterialById(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialByIds", value = "根据id批量获取商城商品信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialByIds")
    public void queryShopMaterialByIds(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialByMaterialIdAndStoreId", value = "根据商品id和门店id获取商城商品信息", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialByMaterialIdAndStoreId")
    public void queryShopMaterialByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialByMaterialIdAndStoreId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShopMaterialMapByMaterialIdAndStoreId", value = "根据商品id和门店id批量获取商城商品信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id，json数组格式", required = "required,json"),
        @ApiImplicitParam(id = "storeId", name = "storeId", value = "门店id，json数组格式", required = "required,json")})
    @RequestMapping("/post/ShopMaterialStoreController/queryShopMaterialMapByMaterialIdAndStoreId")
    public void queryShopMaterialMapByMaterialIdAndStoreId(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.queryShopMaterialMapByMaterialIdAndStoreId(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteShopMaterialStoreByStoreIds", value = "根据门店id删除商城商品信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "storeIds", name = "storeIds", value = "门店id，多个逗号隔开", required = "required")})
    @RequestMapping("/post/ShopMaterialStoreController/deleteShopMaterialStoreByStoreIds")
    public void deleteShopMaterialStoreByStoreIds(InputObject inputObject, OutputObject outputObject) {
        shopMaterialStoreService.deleteShopMaterialStoreByStoreIds(inputObject, outputObject);
    }

}
