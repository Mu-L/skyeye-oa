/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.trip.entity.BusinessTripTimeSlot;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BusinessTripTimeSlotService
 * @Description: 出差申请出差时间段信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/3 10:47
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface BusinessTripTimeSlotService extends SkyeyeLinkDataService<BusinessTripTimeSlot> {
    Map<String, List<BusinessTripTimeSlot>> queryBusinessTripTimeSlotListByBusinessTripIds(List<String> allBusinessTripIds);

    List<BusinessTripTimeSlot> queryBusinessTripTimeSlotByIdsAndTime(List<String> tripIds, String startTime, String endTime);
}
