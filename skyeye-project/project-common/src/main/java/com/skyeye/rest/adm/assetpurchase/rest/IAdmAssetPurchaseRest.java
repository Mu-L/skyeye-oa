package com.skyeye.rest.adm.assetpurchase.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-adm}", configuration = ClientConfiguration.class)
public interface IAdmAssetPurchaseRest {

    /**
     * 获取adm上个月资产采购成本
     *
     */
    @PostMapping("/queryLastMonthAssetPurchaseCost")
    String queryLastMonthAssetPurchaseCost();
}
