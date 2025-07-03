package com.skyeye.rest.scheduling.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-checkwork}", configuration = ClientConfiguration.class)
public interface ISchedulingRest {

    @PostMapping("/deleteSchedulingByWorkId")
    String deleteSchedulingByWorkId(@RequestParam("workId") String workId);
}
