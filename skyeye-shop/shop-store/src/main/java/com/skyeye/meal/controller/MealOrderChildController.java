/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.meal.service.MealOrderChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MealOrderChildController
 * @Description: 套餐订单所选套餐控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 9:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "套餐订单所选套餐", tags = "套餐订单所选套餐", modelName = "套餐订单管理")
public class MealOrderChildController {

    @Autowired
    private MealOrderChildService mealOrderChildService;

    @ApiOperation(id = "queryMealMationByObjectId", value = "根据会员/客户id获取已经购买的套餐信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MealController/queryMealMationByObjectId")
    public void queryMealMationByObjectId(InputObject inputObject, OutputObject outputObject) {
        mealOrderChildService.queryMealMationByObjectId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMealMationByMaterial", value = "根据商品规格以及条形码获取已经购买的套餐信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "会员/客户id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required"),
        @ApiImplicitParam(id = "codeNum", name = "codeNum", value = "规格编号")})
    @RequestMapping("/post/MealController/queryMealMationByMaterial")
    public void queryMealMationByMaterial(InputObject inputObject, OutputObject outputObject) {
        mealOrderChildService.queryMealMationByMaterial(inputObject, outputObject);
    }

}
