package com.skyeye.product.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductLead;

public interface ProductLeadService extends SkyeyeBusinessService<ProductLead> {

    void productLeadToContractOutStock(InputObject inputObject, OutputObject outputObject);

    void updateLeadType(String farmId);

}
