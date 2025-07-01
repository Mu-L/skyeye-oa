package com.skyeye.rest.promote.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.rest.ISysEveUserStaffRest;
import com.skyeye.rest.promote.service.ISysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ISysEveUserStaffServiceImpl extends IServiceImpl implements ISysEveUserStaffService {

    @Autowired
    private ISysEveUserStaffRest iSysEveUserStaffRest;

    @Override
    public List<Map<String, Object>> queryAllStaffList() {
        return ExecuteFeignClient.get(() -> iSysEveUserStaffRest.commonselpeople007()).getRows();
    }

}
