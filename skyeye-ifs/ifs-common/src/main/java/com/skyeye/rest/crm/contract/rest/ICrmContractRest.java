package com.skyeye.rest.crm.contract.rest;


import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-crm}", configuration = ClientConfiguration.class)
public interface ICrmContractRest {

    /**
     * 根据ids获取客户合同信息
     *
     * @param ids 主键ids
     */
    @PostMapping("/queryCrmContractByIds")
    String queryCrmContractByIds(@RequestParam("ids") String ids);

}
