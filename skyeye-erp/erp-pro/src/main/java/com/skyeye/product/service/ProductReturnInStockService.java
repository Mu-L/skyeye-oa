package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductReturnInStock;

public interface ProductReturnInStockService extends SkyeyeFlowableService<ProductReturnInStock> {

    void insertProductReturnInStockToInDepot(InputObject inputObject, OutputObject outputObject);

    void updateOtherState(String fromId);

}
