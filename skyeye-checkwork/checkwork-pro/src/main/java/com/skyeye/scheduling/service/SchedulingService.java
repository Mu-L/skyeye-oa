package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.Scheduling;

public interface SchedulingService extends SkyeyeBusinessService<Scheduling> {

    void writeAutoScheduling(InputObject inputObject, OutputObject outputObject);

    void querySchedulingListByTimeSlot(InputObject inputObject, OutputObject outputObject);

    void deleteSchedulingByIds(InputObject inputObject, OutputObject outputObject);
}
