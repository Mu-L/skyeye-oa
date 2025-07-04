package com.skyeye.rest.checkwork.scheduling.service;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.checkwork.scheduling.rest.ISchedulingRest;
import com.skyeye.rest.checkwork.scheduling.ISchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ISchedulingServiceImpl extends IServiceImpl implements ISchedulingService {

    @Autowired
    private ISchedulingRest iSchedulingRest;
    @Override
    public void deleteSchedulingByWorkId(String workId) {
         ExecuteFeignClient.get(() -> iSchedulingRest.deleteSchedulingByWorkId(workId));
    }
}
