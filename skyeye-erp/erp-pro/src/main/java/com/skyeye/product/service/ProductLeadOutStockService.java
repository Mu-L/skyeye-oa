package com.skyeye.product.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductLeadOutStock;

import java.util.List;

public interface ProductLeadOutStockService extends SkyeyeErpOrderService<ProductLeadOutStock> {
    void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject);

    List<ProductLeadOutStock> queryLeadByHolderId(String holderId);

}
