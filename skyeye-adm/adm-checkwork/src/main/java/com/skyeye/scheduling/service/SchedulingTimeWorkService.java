package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheduling.entity.SchedulingTimeWork;

import java.util.List;

public interface SchedulingTimeWorkService extends SkyeyeBusinessService<SchedulingTimeWork> {

    List<SchedulingTimeWork> querySchedulingTimeByTimeIdAndId(String id, List<String> collect);

    void deleteBySchedulingTimeIds(List<String> schedulingTimeIds);

    List<SchedulingTimeWork> querySchedulingTimeWorkBySchedulingIdAndId(String schedulingId, String id);

    void deleteBySchedulingTimeWorkIds(List<String> allDeleteIds);

    List<SchedulingTimeWork> querySchedulingTimeByIds(List<String> workIds);

    void deleteBySchedulingIds(List<String> ids);
}
