package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.order.entity.OrderItem;
import org.aspectj.weaver.ast.Or;

import java.util.List;
import java.util.Map;

public interface OrderItemService extends SkyeyeBusinessService<OrderItem> {


    void deleteByPerentIds(List<String> ids);

//    List<OrderItem> selectByParentId(String id);

    List<OrderItem> queryListByStateAndOrderId(String orderId, Integer state);

    Map<String, List<OrderItem>> queryListByParentId(String... idList);
}
