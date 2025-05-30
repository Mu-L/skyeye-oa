package com.skyeye.rest.erp.payable.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpPayableRest {

    /**
     * 根据id获取供应商应付信息
     *
     * @param ids 主键ids
     */
    @PostMapping("/queryPayableByIds")
    String queryPayableByIds(@RequestParam("ids") String ids);

}
