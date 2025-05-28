package com.skyeye.rest.promote.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.rest.IProCompanyJobRest;
import com.skyeye.rest.promote.service.IProCompanyJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IProCompanyJobServiceImpl extends IServiceImpl implements IProCompanyJobService {

    @Autowired
    private IProCompanyJobRest iCompanyJobRest;

    @Override
    public List<Map<String, Object>> queryCompanyJobList() {
        return ExecuteFeignClient.get(() -> iCompanyJobRest.queryCompanyJobList()).getRows();
    }
}
