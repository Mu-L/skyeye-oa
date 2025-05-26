package com.skyeye.rest.crm.payment.rest;


import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-crm}", configuration = ClientConfiguration.class)
public interface ICrmPaymentCollectionRest {

    /**
     * 根据id获取客户回款信息
     *
     * @param id 主键id
     */
    @PostMapping("/queryPaymentCollectionById")
    String queryPaymentCollectionById(@RequestParam("id") String id);

}
