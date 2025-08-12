package com.skyeye.school.schedules.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.schedules.entity.Schedule;

/**
 * @ClassName: ScheduleService
 * @Description: 排课表接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */

public interface ScheduleService extends SkyeyeBusinessService<Schedule> {
    void querySchedulesList(InputObject inputObject, OutputObject outputObject);

    void queryMySchedulesList(InputObject inputObject, OutputObject outputObject);

    void querySchedulesInfoList(InputObject inputObject, OutputObject outputObject);

    void queryScheduleList(InputObject inputObject, OutputObject outputObject);
}
