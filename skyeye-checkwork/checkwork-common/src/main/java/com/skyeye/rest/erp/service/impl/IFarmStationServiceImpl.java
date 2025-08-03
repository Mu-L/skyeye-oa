package com.skyeye.rest.erp.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.rest.IFarmStationRest;
import com.skyeye.rest.erp.service.IFarmStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IFarmStationServiceImpl extends IServiceImpl implements IFarmStationService {

    @Autowired
    private IFarmStationRest iFarmStationRest;

    @Override
    public List<Map<String, Object>> queryFarmStationById(String workId) {
        return ExecuteFeignClient.get(() -> iFarmStationRest.queryFarmStationById(workId)).getRows();
    }

    @Override
    public List<Map<String, Object>> queryFarmStationByIds(String workIds) {
        return ExecuteFeignClient.get(() -> iFarmStationRest.queryFarmStationByIds(workIds)).getRows();
    }
}
