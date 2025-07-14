package com.skyeye.rest.checkwork.checkwork.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-checkwork}", configuration = ClientConfiguration.class)
public interface ICheckWorkRest {

    @PostMapping("/queryInfoByStaffIdsAndDates")
    String queryInfoByStaffIdsAndDates(@RequestParam("staffIds") String staffIds, @RequestParam("dates") String dates);
}
