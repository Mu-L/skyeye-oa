package com.skyeye.school.schedules.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.schedules.entity.ScheduleChild;

import java.util.List;


/**
 * @ClassName: ScheduleChildService
 * @Description: 排课表子表接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */
public interface ScheduleChildService extends SkyeyeBusinessService<ScheduleChild> {
    void deleteByScheduleId(String id);

    void writeScheduleChildList(String parentId, List<ScheduleChild> scheduleChildList);

    void updateScheduleChildList(String parentId, List<ScheduleChild> scheduleChildList);

    List<ScheduleChild> queryMyScheduleBySemesterIdAndWeek(String userId, String semesterId, Integer week);
}
