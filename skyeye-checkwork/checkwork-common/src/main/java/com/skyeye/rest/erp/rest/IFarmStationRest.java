package com.skyeye.rest.erp.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IFarmStationRest {

    /**
     * 查询临时员工的工位
     *
     */
    @PostMapping("/queryAllFarmStationList")
    String queryAllFarmStationList();
}
