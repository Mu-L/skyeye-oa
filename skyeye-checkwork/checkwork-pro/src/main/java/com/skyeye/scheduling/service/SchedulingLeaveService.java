package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.SchedulingLeave;

import java.util.List;
import java.util.Map;

public interface SchedulingLeaveService extends SkyeyeBusinessService<SchedulingLeave> {

    void querySchedulingLeaveList(InputObject inputObject, OutputObject outputObject);

//    void updateSchedulingLeave(InputObject inputObject, OutputObject outputObject);

    Map<String, List<SchedulingLeave>> queryLeaveByEmployeeIds(List<String> id, String startTime, String endTime);

}
