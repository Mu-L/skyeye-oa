package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.Order;
import com.skyeye.order.enums.ShopOrderCommentState;

public interface OrderService extends SkyeyeBusinessService<Order> {
    void cancelOrder(InputObject inputObject, OutputObject outputObject);

    void finishOrder(InputObject inputObject, OutputObject outputObject);

    void payOrder(InputObject inputObject, OutputObject outputObject);

    void deliverGoodsByOrderId(InputObject inputObject, OutputObject outputObject);

    void updateCommonState(String id, Integer state);

    void queryOrderPageList(InputObject inputObject, OutputObject outputObject);
}
