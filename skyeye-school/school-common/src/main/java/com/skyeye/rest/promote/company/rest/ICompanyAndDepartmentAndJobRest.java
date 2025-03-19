package com.skyeye.rest.promote.company.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ICompanyAndDepartmentAndJobRest {

    @PostMapping("queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId")
    String queryCompanyInfoByCompanyIdAndDepartmentIdAndJobId(@RequestParam(value = "companyId",required = false) String companyId,
                                                              @RequestParam(value = "departmentId",required = false) String departmentId,
                                                              @RequestParam(value = "jobId",required = false) String jobId);
}
