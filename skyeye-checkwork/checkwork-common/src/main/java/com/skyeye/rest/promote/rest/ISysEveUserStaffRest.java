package com.skyeye.rest.promote.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    /**
     * 查询所有未离职员工
     *
     */
    @PostMapping("/queryAllStaffList")
    String queryAllStaffList();

    /**
     * 根据员工Ids查询所员工
     *
     */
    @PostMapping("/queryEmployeeListByIds")
    String queryEmployeeListByIds(@RequestParam("employeeIds")String employeeIds);
}
