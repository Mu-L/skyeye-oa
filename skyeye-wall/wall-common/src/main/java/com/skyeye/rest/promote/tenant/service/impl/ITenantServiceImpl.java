/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.promote.tenant.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.tenant.rest.ITenantRest;
import com.skyeye.rest.promote.tenant.service.ITenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class ITenantServiceImpl extends IServiceImpl implements ITenantService {

    @Autowired
    private ITenantRest iTenantRest;

    @Override
    public Map<String, Object> queryTenantById(String tenantId) {
        return ExecuteFeignClient.get(() -> iTenantRest.queryTenantById(tenantId)).getBean();
    }
}
