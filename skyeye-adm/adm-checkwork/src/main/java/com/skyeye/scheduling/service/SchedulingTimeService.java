package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheduling.entity.SchedulingTime;

import java.util.List;

public interface SchedulingTimeService extends SkyeyeBusinessService<SchedulingTime> {
    List<SchedulingTime> querySchedulingTimeBySchedulingId(String id);

    void deleteBySchedulingTimeIds(List<String> deleteSchedulingTimeIds);

    List<SchedulingTime> querySchedulingTimeByTimeIds(List<String> timeIds);

    void deleteBySchedulingIds(List<String> ids);

    List<SchedulingTime> querySchedulingTimeByIds(List<String> schedulingTimeIds);
}
