package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.SchedulingShifts;

import java.util.List;

public interface SchedulingShiftsService extends SkyeyeBusinessService<SchedulingShifts> {

    void deleteSchedulingShifts(InputObject inputObject, OutputObject outputObject);

    void querySchedulingShiftsList(InputObject inputObject, OutputObject outputObject);

    List<SchedulingShifts> querySchedulingShiftsByIds(List<String> shiftIds);

    List<SchedulingShifts> querySchedulingShiftsByIdName(List<String> shiftIdList, String keyword);
}
