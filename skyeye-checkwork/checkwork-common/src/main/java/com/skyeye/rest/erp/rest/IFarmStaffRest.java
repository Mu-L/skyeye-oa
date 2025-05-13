package com.skyeye.rest.erp.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IFarmStaffRest {

    /**
     * 查询员工Id对应车间Id
     *
     */
    @PostMapping("/queryAllFarmStaffList")
    String queryAllFarmStaffList();
}
