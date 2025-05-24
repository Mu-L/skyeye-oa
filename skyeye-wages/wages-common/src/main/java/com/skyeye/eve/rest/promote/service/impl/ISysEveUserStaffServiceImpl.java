/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.rest.promote.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.eve.rest.promote.rest.ISysEveUserStaffRest;
import com.skyeye.eve.rest.promote.service.ISysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ISysEveUserStaffServiceImpl extends IServiceImpl implements ISysEveUserStaffService {

    @Autowired
    private ISysEveUserStaffRest iSysEveUserStaffRest;

    @Override
    public List<String> queryTenantUserStaffIdByTenantId(String tenantId) {
        List<Map<String, Object>> tenantUserStaff = ExecuteFeignClient.get(() -> iSysEveUserStaffRest.queryTenantUserStaffIdByTenantId(tenantId)).getRows();
        if (CollectionUtil.isEmpty(tenantUserStaff)) {
            return CollectionUtil.newArrayList();
        }
        List<String> staffId = tenantUserStaff.stream().map(item -> item.get("staffId").toString()).collect(Collectors.toList());
        return staffId;
    }

    @Override
    public void editSysUserStaffActMoneyById(String staffId, String actMoney) {
        Map<String, Object> map = new HashMap<>();
        map.put("staffId", staffId);
        map.put("actMoney", actMoney);
        ExecuteFeignClient.get(() -> iSysEveUserStaffRest.editSysUserStaffActMoneyById(map));
    }

}
