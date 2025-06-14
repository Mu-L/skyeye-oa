package com.skyeye.seal.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.seal.entity.SalesExchanges;

public interface SalesExchangesService extends SkyeyeErpOrderService<SalesExchanges> {
    void querySalesExchangesToDepotPutById(InputObject inputObject, OutputObject outputObject);

    void insertSalesExchangesToTurnDepot(InputObject inputObject, OutputObject outputObject);

    void querySalesExchangesToSalesOutLetById(InputObject inputObject, OutputObject outputObject);

    void insertSalesExchangesToSalesOutLet(InputObject inputObject, OutputObject outputObject);
}
