/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.farm.entity.FarmCalendar;

import java.util.List;

/**
 * @ClassName: FarmCalendarService
 * @Description: 车间产能日历服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FarmCalendarService extends SkyeyeBusinessService<FarmCalendar> {

    /**
     * 根据车间ID和日期获取当日可用工时(分钟)。
     *
     * @param farmId  车间ID
     * @param dateStr 日期字符串，格式：yyyy-MM-dd
     * @return 当日可用工时，未匹配到返回null
     */
    Integer getDailyWorkMinutesByDate(String farmId, String dateStr);

    /**
     * 查询车间下所有启用的产能日历配置
     */
    List<FarmCalendar> listByFarmId(String farmId);
}
