/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.whole.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.entity.PurchaseReturn;
import com.skyeye.whole.entity.WholeOrderOut;
import com.skyeye.whole.service.WholeOrderOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WholeOrderOutController
 * @Description: 整单委外单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/22 20:37
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "整单委外单", tags = "整单委外单", modelName = "整单委外单")
public class WholeOrderOutController {

    @Autowired
    private WholeOrderOutService wholeOrderOutService;

    /**
     * 获取整单委外单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryWholeOrderOutList", value = "获取整单委外单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WholeOrderOutController/queryWholeOrderOutList")
    public void queryWholeOrderOutList(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.queryPageList(inputObject, outputObject);
    }

    /**
     * 不分页获取整单委外单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryNoPageWholeOrderOutList", value = "不分页获取整单委外单列表", method = "POST", allUse = "2")
    @RequestMapping("/post/WholeOrderOutController/queryNoPageWholeOrderOutList")
    public void queryNoPageWholeOrderOutList(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.queryNoPageWholeOrderOutList(inputObject, outputObject);
    }

    /**
     * 新增/编辑整单委外单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeWholeOrderOut", value = "新增/编辑整单委外单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = WholeOrderOut.class)
    @RequestMapping("/post/WholeOrderOutController/writeWholeOrderOut")
    public void writeWholeOrderOut(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转采购入库单/到货单/采购退货单时，根据id查询整单委外单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryWholeOrderOutTransById", value = "转采购入库单/到货单/采购退货单/采购换货单时，根据id查询整单委外单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WholeOrderOutController/queryWholeOrderOutTransById")
    public void queryWholeOrderOutTransById(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.queryWholeOrderOutTransById(inputObject, outputObject);
    }

    /**
     * 整单委外单信息转采购入库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertWholeOrderOutToTurnPut", value = "整单委外单信息转采购入库", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchasePut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WholeOrderOutController/insertWholeOrderOutToTurnPut")
    public void insertWholeOrderOutToTurnPut(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.insertWholeOrderOutToTurnPut(inputObject, outputObject);
    }

    /**
     * 整单委外单信息转到货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertWholeOrderOutToTurnDelivery", value = "整单委外单信息转到货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseDelivery.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseDeliveryController/insertWholeOrderOutToTurnDelivery")
    public void insertWholeOrderOutToTurnDelivery(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.insertWholeOrderOutToTurnDelivery(inputObject, outputObject);
    }

    /**
     * 整单委外单信息转采购退货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertWholeOrderOutToReturns", value = "整单委外单信息转采购退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseReturn.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WholeOrderOutController/insertWholeOrderOutToReturns")
    public void insertWholeOrderOutToReturns(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.insertWholeOrderOutToReturns(inputObject, outputObject);
    }

    /**
     * 整单委外单信息转采购换货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertWholeOrderOutToExchanges", value = "整单委外单信息转采购换货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseExchange.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/WholeOrderOutController/insertWholeOrderOutToExchanges")
    public void insertWholeOrderOutToExchanges(InputObject inputObject, OutputObject outputObject) {
        wholeOrderOutService.insertWholeOrderOutToExchanges(inputObject, outputObject);
    }
}
