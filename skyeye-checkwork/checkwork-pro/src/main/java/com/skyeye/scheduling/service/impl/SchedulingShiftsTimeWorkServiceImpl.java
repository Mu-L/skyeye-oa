package com.skyeye.scheduling.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.scheduling.dao.SchedulingShiftsTimeWorkDao;
import com.skyeye.scheduling.entity.SchedulingShiftsTimeWork;
import com.skyeye.scheduling.service.SchedulingShiftsTimeWorkService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "排班班次时间下工位管理", groupName = "排班班次时间下工位管理")
public class SchedulingShiftsTimeWorkServiceImpl extends SkyeyeBusinessServiceImpl<SchedulingShiftsTimeWorkDao, SchedulingShiftsTimeWork> implements SchedulingShiftsTimeWorkService {
}
