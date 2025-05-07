/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.trip.entity.BusinessTrip;
import com.skyeye.trip.entity.BusinessTripTimeSlot;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CheckWorkBusinessTripService
 * @Description: 出差申请服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface BusinessTripService extends SkyeyeFlowableService<BusinessTrip> {

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     *
     * @param userId 用户id
     * @param timeId 班次id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    List<Map<String, Object>> queryStateIsSuccessBusinessTripDayByUserIdAndMonths(String userId, String timeId,
                                                                                  List<String> months);

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的出差申请数据
     * @param employeeId
     */
    void queryYesDoTime(String employeeId);

    Map<String, List<BusinessTripTimeSlot>> queryStateIsSuccessBusinessTripDayByUserId(String startTime, String endTime);
}
