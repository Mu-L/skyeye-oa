package com.skyeye.purchase.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.purchase.entity.PurchaseExchange;

public interface PurchaseExchangesService extends SkyeyeErpOrderService<PurchaseExchange> {
    void queryPurchaseExchangesTransToDeliveryById(InputObject inputObject, OutputObject outputObject);

    void insertPurchaseExchangesToDelivery(InputObject inputObject, OutputObject outputObject);

    void editArrivalState(String id, Integer arrivalState);

    void editQualityInspection(String id, Integer qualityInspection);
}
