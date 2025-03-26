package com.skyeye.rest.promote.company.rest;

import com.skyeye.common.client.ClientConfiguration;
import com.skyeye.common.entity.search.CommonPageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    @PostMapping("querySysUserStaffList")
    String querySysUserStaffList(CommonPageInfo commonPageInfo);
}
