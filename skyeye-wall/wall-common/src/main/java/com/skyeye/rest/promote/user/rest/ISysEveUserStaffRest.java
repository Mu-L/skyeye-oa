/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.promote.user.rest;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface ISysEveUserStaffRest {

    /**
     * 修改当前登录员工表白墙的背景图
     *
     * @param params 参数，包含：backgroundImage
     * @return
     */
    @PostMapping("updateCurrentUserBgImg")
    String updateCurrentUserBgImg(Map<String, Object> params);
}
