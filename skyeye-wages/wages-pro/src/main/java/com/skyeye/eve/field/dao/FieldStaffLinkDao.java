/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.field.entity.FieldStaffLink;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FieldStaffLinkDao
 * @Description: 员工与薪资字段关系管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 23:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FieldStaffLinkDao extends SkyeyeBaseMapper<FieldStaffLink> {

    void saveStaffWagesModelFieldMation(@Param("list") List<Map<String, Object>> wagesModelFieldMation);

    /**
     * 获取一条还未计算上个月薪资的员工信息(不包含本月刚入职的新员工)
     *
     * @param lastMonthDate 上个月的年月
     * @param staffId       不包含的员工id
     * @return
     */
    @IgnoreTenant
    Map<String, Object> queryNoWagesLastMonthByLastMonthDate(@Param("lastMonthDate") String lastMonthDate,
                                                             @Param("list") List<String> staffId,
                                                             @Param("tenantId") String tenantId);

    /**
     * 获取上个月指定员工的所有考勤记录信息
     *
     * @param staffId       员工id
     * @param lastMonthDate 上个月的年月
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryLastMonthCheckWork(@Param("staffId") String staffId,
                                                      @Param("lastMonthDate") String lastMonthDate,
                                                      @Param("tenantId") String tenantId);

    /**
     * 获取上个月指定员工的所有审批通过请假记录信息
     *
     * @param staffId            员工id
     * @param lastMonthDate      上个月的年月
     * @param leaveTimeState     请假记录状态  {@link com.skyeye.common.enumeration.FlowableStateEnum}
     * @param leaveTimeSlotState 请假时间段状态 {@link com.skyeye.common.enumeration.FlowableChildStateEnum}
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryLastMonthLeaveTime(@Param("staffId") String staffId,
                                                      @Param("lastMonthDate") String lastMonthDate,
                                                      @Param("leaveTimeState") String leaveTimeState,
                                                      @Param("leaveTimeSlotState") String leaveTimeSlotState,
                                                      @Param("tenantId") String tenantId);

    /**
     * 获取上个月指定员工的所有审批通过销假记录信息
     *
     * @param staffId            员工id
     * @param lastMonthDate      上个月的年月
     * @param leaveTimeState     请假记录状态  {@link com.skyeye.common.enumeration.FlowableStateEnum}
     * @param leaveTimeSlotState 请假时间段状态 {@link com.skyeye.common.enumeration.FlowableChildStateEnum}
     * @return
     */
    @IgnoreTenant
    List<Map<String, Object>> queryLastMonthCancleLeaveTime(@Param("staffId") String staffId,
                                                            @Param("lastMonthDate") String lastMonthDate,
                                                            @Param("leaveTimeState") String leaveTimeState,
                                                            @Param("leaveTimeSlotState") String leaveTimeSlotState,
                                                            @Param("tenantId") String tenantId);

    /**
     * 将指定员工月度清零的薪资字段设置为0
     *
     * @param staffId 员工id
     */
    @IgnoreTenant
    void editStaffMonthlyClearingWagesByStaffId(@Param("staffId") String staffId, @Param("tenantId") String tenantId);

}
