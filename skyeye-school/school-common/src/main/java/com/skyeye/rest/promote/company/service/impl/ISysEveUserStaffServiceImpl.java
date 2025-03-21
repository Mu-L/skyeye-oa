package com.skyeye.rest.promote.company.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.company.rest.ISysEveUserStaffRest;
import com.skyeye.rest.promote.company.service.ISysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ISysEveUserStaffServiceImpl extends IServiceImpl implements ISysEveUserStaffService {

    @Autowired
    private ISysEveUserStaffRest iSysEveUserStaffRest;

    @Override
    public List<Map<String, Object>> queryUserMationList(String userIds, String staffIds) {
        return ExecuteFeignClient.get(() -> iSysEveUserStaffRest.queryUserMationList(userIds, staffIds)).getRows();
    }

}
