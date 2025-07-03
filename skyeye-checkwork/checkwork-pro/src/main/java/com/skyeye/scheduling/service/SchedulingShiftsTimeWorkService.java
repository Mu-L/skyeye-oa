package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.scheduling.entity.SchedulingShiftsTimeWork;

import java.util.List;

public interface SchedulingShiftsTimeWorkService extends SkyeyeBusinessService<SchedulingShiftsTimeWork> {
    List<SchedulingShiftsTimeWork> queryShiftsTimeWorkByShiftsTimeIds(List<String> shiftsTimeIds);

    void deleteShiftsTimeWorkByShiftsTimeIds(List<String> shiftsTimeIds);

    List<SchedulingShiftsTimeWork> queryShiftsTimeWorkByIds(List<String> schedulingShiftsTimeWorkId);

    void deleteSchedulingByWorkId(InputObject inputObject, OutputObject outputObject);
}
