package com.skyeye.rest.erp.farm.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IFarmStationRest {

    /**
     * 查询临时员工的工位
     *
     */
    @PostMapping("/queryFarmStationById")
    String queryFarmStationById(@RequestParam("workId") String workId);

    /**
     * 批量查询临时员工的工位
     *
     */
    @PostMapping("/queryFarmStationByIds")
    String queryFarmStationByIds(@RequestParam("workIds") String workIds);
}
