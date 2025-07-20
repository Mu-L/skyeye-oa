/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.OrderComment;
import com.skyeye.order.service.OrderCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OrderCommentController
 * @Description: 商品订单评价管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "商品订单评价管理", tags = "商品订单评价管理", modelName = "商品订单评价管理")
public class OrderCommentController {

    @Autowired
    private OrderCommentService orderCommentService;

    @ApiOperation(id = "insertOrderComment", value = "新增商品订单评价信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = OrderComment.class)
    @RequestMapping("/post/OrderCommentController/insertOrderComment")
    public void insertOrderComment(InputObject inputObject, OutputObject outputObject) {
        orderCommentService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteOrderCommentById", value = "根据id删除商品订单评价信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderCommentController/deleteOrderCommentById")
    public void deleteOrderCommentById(InputObject inputObject, OutputObject outputObject) {
        orderCommentService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "selectOrderCommentById", value = "根据id查询商品订单评价信息", method = "POST", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderCommentController/selectOrderCommentById")
    public void selectOrderCommentById(InputObject inputObject, OutputObject outputObject) {
        orderCommentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderCommentPageList", value = "分页查询商品订单评价信息", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderCommentController/queryOrderCommentPageList")
    public void queryOrderCommentPageList(InputObject inputObject, OutputObject outputObject) {
        orderCommentService.queryOrderCommentPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderCommentPageListPC", value = "分页查询商品订单评价信息PC", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
            @ApiImplicitParam(id = "type", name = "type", value = "查询类型，Store：门店；All：所有，", required = "required"),
            @ApiImplicitParam(id = "holderId", name = "holderId", value = "门店id,type为'All'时，传了holderId也不会生效"),
            @ApiImplicitParam(id = "keyword", name = "keyword", value = "订单编号")})
    @RequestMapping("/post/OrderCommentController/queryOrderCommentPageListPC")
    public void queryOrderCommentPageListPC(InputObject inputObject, OutputObject outputObject) {
        orderCommentService.queryOrderCommentPageListPC(inputObject,outputObject);
    }

    @ApiOperation(id = "queryMyOrderCommentList", value = "分页查询自己的商品订单评价信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderCommentController/queryMyOrderCommentList")
    public void queryMyOrderCommentList(InputObject inputObject, OutputObject outputObject) {
        orderCommentService.queryMyOrderCommentList(inputObject, outputObject);
    }
}