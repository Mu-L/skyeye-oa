package com.skyeye.rest.erp.equipment.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-erp}", configuration = ClientConfiguration.class)
public interface IErpEquipmentRest {

    /**
     * 获取erp上个月设备成本
     *
     */
    @PostMapping("/queryLastMonthEquipmentCost")
    String queryLastMonthEquipmentCost();
}
