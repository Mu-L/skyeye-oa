package com.skyeye.rest.promote.company.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-promote}", configuration = ClientConfiguration.class)
public interface ICompanyRest {

    @PostMapping("queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId")
    String queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(@RequestParam("companyId") String companyId,
                                                              @RequestParam("departmentId") String departmentId,
                                                              @RequestParam("jobId") String jobId);

}
