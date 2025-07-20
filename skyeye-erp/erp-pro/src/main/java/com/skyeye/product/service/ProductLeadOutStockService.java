package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductLeadOutStock;

import java.util.List;

public interface ProductLeadOutStockService extends SkyeyeFlowableService<ProductLeadOutStock> {
    void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject);

    List<ProductLeadOutStock> queryLeadByHolderId(String holderId);

    List<ProductLeadOutStock> queryByIds(List<String> framIds);

    void editOtherState(String fromId, Integer key);

}
