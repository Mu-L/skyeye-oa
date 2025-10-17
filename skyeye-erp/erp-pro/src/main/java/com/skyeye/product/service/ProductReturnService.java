package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductReturn;

import java.util.List;
import java.util.Map;

public interface ProductReturnService extends SkyeyeBusinessService<ProductReturn> {
    void productLeadToContractOutStock(InputObject inputObject, OutputObject outputObject);

    void updateOtherState(String fromId);
}
