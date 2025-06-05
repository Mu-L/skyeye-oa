/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.CheckDayType;
import com.skyeye.common.enumeration.FlowableChildStateEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.OvertimeSettlementType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.centerrest.entity.checkwork.UserStaffHolidayRest;
import com.skyeye.eve.centerrest.user.SysEveUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.overtime.classenum.OvertimeSoltSettleState;
import com.skyeye.overtime.dao.OvertimeDao;
import com.skyeye.overtime.entity.OverTime;
import com.skyeye.overtime.entity.OverTimeSlot;
import com.skyeye.overtime.service.OverTimeSlotService;
import com.skyeye.overtime.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OvertimeServiceImpl
 * @Description: 加班申请服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/8 22:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "加班申请", groupName = "加班申请", flowable = true)
public class OvertimeServiceImpl extends SkyeyeFlowableServiceImpl<OvertimeDao, OverTime> implements OvertimeService {

    @Autowired
    private OverTimeSlotService overTimeSlotService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private SysEveUserService sysEveUserService;

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryOvertimeList(pageInfo);
        return beans;
    }

    @Override
    public void validatorEntity(OverTime entity) {
        chectOrderItem(entity.getOverTimeSlotList());
    }

    @Override
    public void writeChild(OverTime entity, String userId) {
        entity.getOverTimeSlotList().forEach(overTimeSlot -> {
            overTimeSlot.setSettleState(OvertimeSoltSettleState.WAIT_STATISTICS.getKey());
        });
        overTimeSlotService.saveLinkList(entity.getId(), entity.getOverTimeSlotList());
        super.writeChild(entity, userId);
    }

    private void chectOrderItem(List<OverTimeSlot> overTimeSlots) {
        List<String> overtimeDays = overTimeSlots.stream()
            .map(OverTimeSlot::getOvertimeDay).distinct()
            .collect(Collectors.toList());
        if (overTimeSlots.size() != overtimeDays.size()) {
            throw new CustomException("单据中不允许出现同一天的加班日期");
        }
    }

    @Override
    public void submitToApprovalPostpose(String id, String processInstanceId) {
        super.submitToApprovalPostpose(id, processInstanceId);
        overTimeSlotService.editStateByPId(id, FlowableChildStateEnum.IN_EXAMINE.getKey());
    }

    @Override
    public OverTime getDataFromDb(String id) {
        OverTime overTime = super.getDataFromDb(id);
        List<OverTimeSlot> overTimeSlotList = overTimeSlotService.selectByPId(overTime.getId());
        overTime.setOverTimeSlotList(overTimeSlotList);
        return overTime;
    }

    @Override
    public OverTime selectById(String id) {
        OverTime overTime = super.selectById(id);
        overTime.setStateName(FlowableStateEnum.getStateName(overTime.getState()));
        iAuthUserService.setName(overTime, "createId", "createName");
        return overTime;
    }

    @Override
    public void revokePostpose(OverTime entity) {
        super.revokePostpose(entity);
        overTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.DRAFT.getKey());
    }

    @Override
    protected void approvalEndIsSuccess(OverTime entity) {
        calcUserStaffOvertimeMation(entity.getId(), entity.getCreateId());
    }

    @Override
    protected void approvalEndIsFailed(OverTime entity) {
        overTimeSlotService.editStateByPId(entity.getId(), FlowableChildStateEnum.REJECT.getKey());
    }

    /**
     * 校验该单据中的天数是否符合规则
     *
     * @param overtimeId 加班信息id
     * @param createId   创建人id
     */
    private void calcUserStaffOvertimeMation(String overtimeId, String createId) {
        // 用户信息
        Map<String, Object> user = iAuthUserService.queryDataMationById(createId);
        // 用户所在部门的加班结算方式
        int overtimeSettlementType =
            Integer.parseInt(iDepmentService.queryDataMationById(user.get("departmentId").toString()).get("overtimeSettlementType").toString());
        // 员工id
        String staffId = user.get("staffId").toString();
        // 员工当前剩余补休
        String holidayNumber = user.get("holidayNumber").toString();

        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 获取加班天数信息
        List<OverTimeSlot> overTimeSlotList = overTimeSlotService.selectByPId(overtimeId);
        for (OverTimeSlot overTimeSlot : overTimeSlotList) {
            String overtimeSoltId = overTimeSlot.getId();
            String overtimeDay = overTimeSlot.getOvertimeDay();
            String overtimeHour = overTimeSlot.getOvertimeHour();
            List<Map<String, Object>> inData = skyeyeBaseMapper.queryPassThisDayAndCreateId(createId, overtimeDay,
                FlowableChildStateEnum.ADEQUATE.getKey(), tenantId);
            if (CollectionUtil.isEmpty(inData)) {
                // 如果指定天还没有审批通过的记录，则审批通过
                overTimeSlotService.editStateById(overtimeSoltId, FlowableChildStateEnum.ADEQUATE.getKey());
                if (overtimeSettlementType == OvertimeSettlementType.COMPENSATORY_LEAVE_SETTLEMENT.getKey()) {
                    // 补休结算
                    holidayNumber = CalculationUtil.add(holidayNumber, overtimeHour, CommonNumConstants.NUM_TWO);
                    // 修改加班电子流的结算状态
                    overTimeSlotService.editSettleStateById(overtimeSoltId, OvertimeSoltSettleState.RECORDED_IN_STATISTICS.getKey());
                }
                // 修改加班电子流的结算类型
                overTimeSlotService.editSettlementTypeById(overtimeSoltId, overtimeSettlementType);
            } else {
                overTimeSlotService.editStateById(overtimeSoltId, FlowableChildStateEnum.INSUFFICIENT.getKey());
            }
        }
        // 修改员工剩余补休信息
        UserStaffHolidayRest sysUserStaffHoliday = new UserStaffHolidayRest();
        sysUserStaffHoliday.setStaffId(staffId);
        sysUserStaffHoliday.setHolidayNumber(holidayNumber);
        sysUserStaffHoliday.setHolidayStatisTime(DateUtil.getTimeAndToString());
        ExecuteFeignClient.get(() -> sysEveUserService.updateSysUserStaffHolidayNumberById(sysUserStaffHoliday)).getBean();
    }

    /**
     * 获取指定员工在指定月份的所有审核通过的加班申请数据
     *
     * @param userId 用户id
     * @param months 指定月份，月格式（yyyy-MM）
     * @return
     */
    @Override
    public List<Map<String, Object>> queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(String userId, List<String> months) {
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryStateIsSuccessWorkOvertimeDayByUserIdAndMonths(userId, months, tenantId);
        beans.forEach(bean -> {
            bean.put("type", CheckDayType.DAY_IS_WORK_OVERTIME.getKey());
            bean.put("className", CheckDayType.DAY_IS_WORK_OVERTIME.getClassName());
            bean.put("allDay", "1");
            bean.put("showBg", "2");
            bean.put("editable", false);
        });
        return beans;
    }
}
