package com.skyeye.rest.adm.articlespurchase.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IAdmArticlePurchaseRest {

    /**
     * 获取adm上个月商品采购成本
     *
     *
     */
    @PostMapping("/queryLastMonthAssetArticleCost")
    String queryLastMonthAssetArticleCost();
}
