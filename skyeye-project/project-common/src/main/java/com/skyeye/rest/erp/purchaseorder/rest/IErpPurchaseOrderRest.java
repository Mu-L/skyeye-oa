package com.skyeye.rest.erp.purchaseorder.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpPurchaseOrderRest {

    /**
     * erp上个月采购成本
     *
     */
    @PostMapping("/queryLastMonthPurchaseOrderCost")
    String queryLastMonthPurchaseOrderCost();
}
