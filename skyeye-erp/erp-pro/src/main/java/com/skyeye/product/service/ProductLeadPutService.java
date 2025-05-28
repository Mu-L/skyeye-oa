package com.skyeye.product.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.product.entity.ProductLeadPut;

public interface ProductLeadPutService extends SkyeyeErpOrderService<ProductLeadPut> {
    ProductLeadPut queryLendPutByHolderId(String holderId);
}
