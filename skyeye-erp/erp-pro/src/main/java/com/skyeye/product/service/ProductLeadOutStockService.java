package com.skyeye.product.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductLeadOutStock;

public interface ProductLeadOutStockService extends SkyeyeErpOrderService<ProductLeadOutStock> {
    void queryProductLeadOutStockById(InputObject inputObject, OutputObject outputObject);

    void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject);

}
