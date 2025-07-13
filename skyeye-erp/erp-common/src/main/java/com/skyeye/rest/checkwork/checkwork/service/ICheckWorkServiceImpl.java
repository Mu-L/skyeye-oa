package com.skyeye.rest.checkwork.checkwork.service;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.checkwork.checkwork.ICheckWorkService;
import com.skyeye.rest.checkwork.checkwork.rest.ICheckWorkRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ICheckWorkServiceImpl extends IServiceImpl implements ICheckWorkService {

    @Autowired
    private ICheckWorkRest iCheckWorkRest;
    @Override
    public List<Map<String, Object>> queryInfoByStaffIdsAndDates(String staffIds, String dates) {
        return ExecuteFeignClient.get(() -> iCheckWorkRest.queryInfoByStaffIdsAndDates(staffIds, dates)).getRows();
    }
}
