package com.skyeye.rest.school.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-school}", configuration = ClientConfiguration.class)
public interface ISchoolRest {

    @PostMapping("/querySchoolStudentListByNo")
    String querySchoolStudentListByNo(@RequestParam("no") String no,
                                      @RequestParam("id") String id,
                                      @RequestParam("userId") String userId);
}
