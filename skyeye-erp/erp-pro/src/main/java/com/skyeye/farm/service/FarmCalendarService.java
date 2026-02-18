/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.farm.entity.FarmCalendar;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FarmCalendarService
 * @Description: 车间产能日历服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FarmCalendarService extends SkyeyeBusinessService<FarmCalendar> {

    Integer resolveDailyCapFromCalendar(List<FarmCalendar> allList, String dateStr);

    /**
     * 查询车间下所有启用的产能日历配置
     */
    List<FarmCalendar> listByFarmId(String farmId);

    /**
     * 批量查询多个车间下所有启用的产能日历配置，一次查库
     *
     * @param farmIds 车间ID列表
     * @return farmId -> 该车间产能日历列表（按优先级降序）
     */
    Map<String, List<FarmCalendar>> listByFarmIds(List<String> farmIds);
}
