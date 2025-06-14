package com.skyeye.scheduling.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.scheduling.entity.SchedulingTimeWorkPeople;

import java.util.List;

public interface SchedulingTimeWorkPeopleService extends SkyeyeBusinessService<SchedulingTimeWorkPeople> {

    List<SchedulingTimeWorkPeople> queryTimeWorkByThreeId(String id, List<String> timeIds, List<String> timeWorkIds);

    void deleteBySchedulingTimeIdsAndOthorId(List<String> schedulingTimeWorkIds);

    List<SchedulingTimeWorkPeople> queryPeopleByThreeId(String id, String schedulingId, String schedulingTimeId);

    void deleteBySchedulingTimeIds(List<String> deleteIdList);

    void deleteSchedulingTimeWorkPeopleByTimeIds(List<String> allDeleteIds);

    List<SchedulingTimeWorkPeople> querySchedulingByschedulingIdsAndStaffId(List<String> schedulingIds, String staffId);

    void deleteBySchedulingIds(List<String> ids);
}
