package com.skyeye.rest.promote.company.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    @PostMapping("selectByName")
    String selectByName(@RequestParam(value = "serviceClassName", required = false) String serviceClassName,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        @RequestParam(value = "limit") Integer limit,
                        @RequestParam(value = "pages") Integer pages);
}
