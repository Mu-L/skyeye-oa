/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.holder.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.holder.service.HolderNormsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: HolderNormsController
 * @Description: 关联的客户/供应商/会员购买或者出售的商品信息控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/2 21:34
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "关联的客户/供应商/会员购买或者出售的商品信息管理", tags = "关联的客户/供应商/会员购买或者出售的商品信息管理", modelName = "关联的客户/供应商/会员购买或者出售的商品信息管理")
public class HolderNormsController {

    @Autowired
    private HolderNormsService holderNormsService;

    @ApiOperation(id = "queryHolderNormsList", value = "获取关联的客户/供应商/会员购买或者出售的商品信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/HolderNormsController/queryHolderNormsList")
    public void queryHolderNormsList(InputObject inputObject, OutputObject outputObject) {
        holderNormsService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryHolderMaterialListByHolder", value = "根据holder获取商品信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "holderId", name = "holderId", value = "关联的客户/供应商/会员id"),
        @ApiImplicitParam(id = "holderKey", name = "holderKey", value = "关联的客户/供应商/会员的className")})
    @RequestMapping("/post/HolderNormsController/queryHolderMaterialListByHolder")
    public void queryHolderMaterialListByHolder(InputObject inputObject, OutputObject outputObject) {
        holderNormsService.queryHolderMaterialListByHolder(inputObject, outputObject);
    }

    @ApiOperation(id = "queryHolderMaterialNormsListByHolder", value = "根据holder获取商品规格信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id"),
        @ApiImplicitParam(id = "holderId", name = "holderId", value = "关联的客户/供应商/会员id"),
        @ApiImplicitParam(id = "holderKey", name = "holderKey", value = "关联的客户/供应商/会员的className")})
    @RequestMapping("/post/HolderNormsController/queryHolderMaterialNormsListByHolder")
    public void queryHolderMaterialNormsListByHolder(InputObject inputObject, OutputObject outputObject) {
        holderNormsService.queryHolderMaterialNormsListByHolder(inputObject, outputObject);
    }

    @ApiOperation(id = "queryHolderMaterialNormsCodeListByHolder", value = "根据holder获取商品规格对应的条形码信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id"),
        @ApiImplicitParam(id = "holderId", name = "holderId", value = "关联的客户/供应商/会员id"),
        @ApiImplicitParam(id = "holderKey", name = "holderKey", value = "关联的客户/供应商/会员的className")})
    @RequestMapping("/post/HolderNormsController/queryHolderMaterialNormsCodeListByHolder")
    public void queryHolderMaterialNormsCodeListByHolder(InputObject inputObject, OutputObject outputObject) {
        holderNormsService.queryHolderMaterialNormsCodeListByHolder(inputObject, outputObject);
    }

}
