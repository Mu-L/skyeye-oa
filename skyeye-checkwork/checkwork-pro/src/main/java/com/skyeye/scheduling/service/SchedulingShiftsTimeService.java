package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;

import java.util.List;
import java.util.Map;

public interface SchedulingShiftsTimeService extends SkyeyeBusinessService<SchedulingShiftsTime> {

    void deleteSchedulingShiftsTimeByShiftIds(List<String> idList);

    Map<String, List<SchedulingShiftsTime>> queryTimeByIdList(List<String> schedulingShiftsIds);

    List<SchedulingShiftsTime> queryTimeByShiftId(String id);
}
