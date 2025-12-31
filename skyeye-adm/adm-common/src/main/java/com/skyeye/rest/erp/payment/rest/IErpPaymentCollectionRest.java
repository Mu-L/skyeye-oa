package com.skyeye.rest.erp.payment.rest;


import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpPaymentCollectionRest {

    /**
     * 根据id获取供应商付款信息
     *
     * @param ids 主键ids
     */
    @PostMapping("/queryPaymentCollectionByIds")
    String queryPaymentCollectionById(@RequestParam("ids") String ids);

}
