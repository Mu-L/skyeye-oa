package com.skyeye.rest.erp.farm.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.farm.rest.IFarmStaffRest;
import com.skyeye.rest.erp.farm.service.IFarmStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IFarmStaffServiceImpl extends IServiceImpl implements IFarmStaffService {

    @Autowired
    private IFarmStaffRest iFarmStaffRest;

    @Override
    public List<Map<String, Object>> queryStaffByFarmId(String farmId) {
        return ExecuteFeignClient.get(() -> iFarmStaffRest.queryStaffByFarmId(farmId)).getRows();
    }
}
