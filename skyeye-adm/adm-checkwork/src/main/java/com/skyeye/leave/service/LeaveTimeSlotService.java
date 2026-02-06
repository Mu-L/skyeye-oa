/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.leave.entity.LeaveTimeSlot;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LeaveTimeSlotService
 * @Description: 请假申请请假时间段服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/5 8:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface LeaveTimeSlotService extends SkyeyeLinkDataService<LeaveTimeSlot> {

    void editStateById(String id, String state, Integer useYearHoliday);

    Map<String, List<LeaveTimeSlot>> queryLeaveTimeSlotListByLeaveIds(List<String> leaveIds);

    List<LeaveTimeSlot> queryTimeAndIds(List<String> leaveIds, String startTime, String endTime);

    /**
     * 获取上个月指定员工的所有审批通过请假记录信息
     *
     * @param staffId            员工id
     * @param lastMonthDate      上个月的年月，格式：yyyy-MM
     * @param leaveTimeState     请假记录状态  {@link com.skyeye.common.enumeration.FlowableStateEnum}
     * @param leaveTimeSlotState 请假时间段状态 {@link com.skyeye.common.enumeration.FlowableChildStateEnum}
     * @param tenantId           租户id
     * @return 请假记录列表，包含 leaveType、leaveDay、leaveStartTime、leaveEndTime、timeId、useYearHoliday
     */
    List<LeaveTimeSlot> queryLastMonthLeaveTime(String staffId, String lastMonthDate,
                                                String leaveTimeState, String leaveTimeSlotState, String tenantId);

    /**
     * 获取指定日期已经审核通过的请假时间段信息
     *
     * @param timeId   班次id
     * @param createId 创建人/申请人id
     * @param leaveDay 指定日期，格式：yyyy-MM-dd
     * @param tenantId 租户id
     * @return 请假时间段列表
     */
    List<LeaveTimeSlot> queryCheckWorkLeaveSlotByMation(String timeId, String createId, String leaveDay, String tenantId);

    /**
     * 获取指定员工在指定月份和班次的所有审核通过的请假时间段
     *
     * @param userId   用户id
     * @param timeId   班次id
     * @param months   指定月份，月格式（yyyy-MM）
     * @param tenantId 租户id
     * @return 请假时间段列表
     */
    List<LeaveTimeSlot> queryStateIsSuccessLeaveDayByUserIdAndMonths(String userId, String timeId, List<String> months, String tenantId);
}
