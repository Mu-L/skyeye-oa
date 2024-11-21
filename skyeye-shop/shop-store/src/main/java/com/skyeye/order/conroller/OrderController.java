package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.Order;
import com.skyeye.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "商品订单管理", tags = "商品订单管理", modelName = "商品订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 新增商品订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertOrder", value = "新增商品订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Order.class)
    @RequestMapping("/post/OrderController/insertOrder")
    public void insertOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.createEntity(inputObject, outputObject);
    }

    /**
     * 分页获取商品订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryOrderPageListPC", value = "分页获取商品订单信息(后台管理)", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderController/queryOrderPageListPC")
    public void queryOrderPageListPC(InputObject inputObject, OutputObject outputObject) {
        orderService.queryPageList(inputObject, outputObject);
    }

    /**
     * 分页获取商品订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryOrderPageList", value = "分页获取商品订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/OrderController/queryOrderPageList")
    public void queryOrderList(InputObject inputObject, OutputObject outputObject) {
        orderService.queryOrderPageList(inputObject, outputObject);
    }

    /**
     * 批量删除商品订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteOrderByIds", value = "批量删除商品订单信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "ids", name = "ids", value = "主键id,多个id用逗号分隔", required = "required")})
    @RequestMapping("/post/OrderController/deleteOrderByIds")
    public void deleteOrderByIds(InputObject inputObject, OutputObject outputObject) {
        orderService.deleteByIds(inputObject, outputObject);
    }

    /**
     * 根据id查询商品订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectOrderById", value = "根据id查询商品订单信息", method = "POST", allUse = "2")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderController/selectOrderById")
    public void selectOrderById(InputObject inputObject, OutputObject outputObject) {
        orderService.selectById(inputObject, outputObject);
    }

    /**
     * 商品订单取消
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "cancelOrder", value = "商品订单取消", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "cancelType", name = "cancelType", value = "取消类型", required = "required")})
    @RequestMapping("/post/OrderController/cancelOrder")
    public void cancelOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.cancelOrder(inputObject, outputObject);
    }

    /**
     * 商品订单完成
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "finishOrder", value = "商品订单完成", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderController/finishOrder")
    public void finishOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.finishOrder(inputObject, outputObject);
    }

    /**
     * 商品订单支付
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "payOrder", value = "商品订单支付", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "channelCode", name = "channelCode", value = "支付渠道编码", required = "required"),
        @ApiImplicitParam(id = "channelExtras", name = "channelExtras", value = "支付渠道的额外参数，例如说，微信公众号需要传递 openid 参数", required = "json")})
    @RequestMapping("/post/OrderController/payOrder")
    public void payOrder(InputObject inputObject, OutputObject outputObject) {
        orderService.payOrder(inputObject, outputObject);
    }

    /**
     * 商品订单发货
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deliverGoodsByOrderId", value = "商品订单发货", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/OrderController/deliverGoodsByOrderId")
    public void deliverGoodsByOrderId(InputObject inputObject, OutputObject outputObject) {
        orderService.deliverGoodsByOrderId(inputObject, outputObject);
    }
}