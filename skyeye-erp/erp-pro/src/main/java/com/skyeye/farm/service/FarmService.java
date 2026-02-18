/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.Farm;
import com.skyeye.farm.entity.FarmCalendar;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FarmService
 * @Description: 车间管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FarmService extends SkyeyeBusinessService<Farm> {

    List<String> queryFarmIdListByDepartmentId(String departmentId);

    List<Farm> queryFarmListByChargePerson(String userId);

    void queryMyChargeFarmList(InputObject inputObject, OutputObject outputObject);

    void queryEnabledFarmList(InputObject inputObject, OutputObject outputObject);

    List<Farm> queryEnabledFarmList();

    List<Farm> queryFarmListByIds(List<String> farmIds);

    /**
     * 批量获取车间在日期区间内每日可用工时，使用预加载的产能日历规则，避免查库。
     *
     * @param farmId         车间ID
     * @param startDateStr   开始日期 yyyy-MM-dd
     * @param endDateStr     结束日期 yyyy-MM-dd
     * @param calendarList   该车间产能日历列表（由 listByFarmIds 批量查询得到）
     * @param defaultMinutes 车间默认每日工时，未配置日历时使用
     * @return dateStr -> 当日可用工时(分钟)
     */
    Map<String, Integer> getDailyWorkMinutesByDateRange(String farmId, String startDateStr, String endDateStr,
                                                        List<FarmCalendar> calendarList, int defaultMinutes);

}
