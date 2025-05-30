package com.skyeye.rest.promote.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface IProCompanyJobRest {

    /**
     * 查询正式员工的职位信息
     *
     */
    @PostMapping("/queryAllCompanyJobList")
    String queryCompanyJobList();
}
