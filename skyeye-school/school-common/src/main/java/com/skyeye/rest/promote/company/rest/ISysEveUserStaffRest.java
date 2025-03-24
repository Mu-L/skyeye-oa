package com.skyeye.rest.promote.company.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    @PostMapping("queryUserMationList")
    String queryUserMationList(@RequestParam(value = "userIds", required = false) String userIds,
                               @RequestParam(value = "staffIds", required = false) String staffIds);

    @PostMapping("selectByName")
    String selectByName(@RequestParam(value = "serviceClassName", required = false) String serviceClassName,
                        @RequestParam(value = "keyword", required = false) String keyword,
                        @RequestParam(value = "limit") Integer limit,
                        @RequestParam(value = "pages") Integer pages);

    @PostMapping("selectByObjectId")
    String selectByObjectId(@RequestParam(value = "objectId") String objectId);
}
