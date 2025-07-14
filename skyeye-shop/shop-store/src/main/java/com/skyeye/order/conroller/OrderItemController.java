package com.skyeye.order.conroller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "订单子单表管理", tags = "订单子单表管理", modelName = "订单子单表管理")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @ApiOperation(id = "queryOrderItemByStoreId", value = "根据门店Id分页查询", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class, value = {
            @ApiImplicitParam(id = "objectId",  name = "objectId", value = "门店id")})
    @RequestMapping("/post/OrderItemController/queryOrderItemByStoreId")
    public void queryOrderByStoreId(InputObject inputObject, OutputObject outputObject) {
        orderItemService.queryPageList(inputObject, outputObject);
    }

}
