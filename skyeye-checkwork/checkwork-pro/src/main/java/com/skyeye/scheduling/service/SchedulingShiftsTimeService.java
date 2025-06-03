package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheduling.entity.SchedulingShiftsTime;

import java.util.List;
import java.util.Map;

public interface SchedulingShiftsTimeService extends SkyeyeBusinessService<SchedulingShiftsTime> {

    void deleteSchedulingShiftsTimeByShiftIds(List<String> idList);

    List<SchedulingShiftsTime> queryTimeByIdList(List<String> schedulingShiftsIds);

    List<SchedulingShiftsTime> queryTimeByShiftId(String id);
    
    Map<String, List<SchedulingShiftsTime>> queryTimeByIdListMap(List<String> schedulingShiftsIdList);

    List<SchedulingShiftsTime> queryShiftsTimeByIdList(List<String> shiftsTimeIdList);

    List<SchedulingShiftsTime> queryShiftsTimeById(String id);
}
