package com.skyeye.rest.promote.company.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.company.rest.ISysEveUserRest;
import com.skyeye.rest.promote.company.service.ISysEveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ISysEveUserServiceImpl extends IServiceImpl implements ISysEveUserService {

    @Autowired
    private ISysEveUserRest iSysEveUserRest;

    @Override
    public List<Map<String, Object>> queryUserMationList(String userIds, String staffIds) {
        return ExecuteFeignClient.get(() -> iSysEveUserRest.queryUserMationList(userIds,staffIds)).getRows();
    }

}
