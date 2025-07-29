/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sys.quartz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.enumeration.WinterVacationType;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AnnualLeaveStatisticsQuartz
 * @Description: 定时计算员工年假
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/5 19:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
public class AnnualLeaveStatisticsQuartz {

    private static Logger LOGGER = LoggerFactory.getLogger(AnnualLeaveStatisticsQuartz.class);

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 每个季度的第一天零点开始执行员工年假计算任务
     */
    @XxlJob("annualLeaveStatisticsQuartz")
    public void annualLeaveStatistics() {
        LOGGER.info("annualLeaveStatistics start.");
        if (tenantEnable) {
            //  开启多租户
            List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
            if (CollectionUtil.isEmpty(tenantList)) {
                return;
            }
            tenantList.forEach(tenant -> {
                String tenantId = tenant.get("id").toString();
                TenantContext.setTenantId(tenantId);
                calcUserAnnualLeave(tenantId);
            });
        } else {
            calcUserAnnualLeave(null);
        }
        LOGGER.info("annualLeaveStatistics end.");
    }

    private void calcUserAnnualLeave(String tenantId) {
        try {
            // 1.获取年假信息
            List<Map<String, Object>> yearHolidaysMation = getSystemYearHolidaysMation();
            // 2.获取所有在职状态的员工列表, 见习，试用，退休，离职员工不计入计算
            List<SysEveUserStaff> userStaff = sysEveUserStaffService.queryUserStaffByState(UserStaffState.ON_THE_JOB.getKey());
            // 获取当前年月日
            String nowDate = DateUtil.getYmdTimeAndToString();
            for (SysEveUserStaff staff : userStaff) {
                String staffId = staff.getId();
                // 员工当前剩余年假
                String annualLeave = staff.getAnnualLeave();
                // 开始工作时间
                String workTime = staff.getWorkTime();
                // 获取到相差的天数
                int differDays = DateUtil.getDistanceDay(workTime, nowDate);
                String differYear = getDifferYear(differDays);
                Map<String, Object> yearMation = getConcertWithYearMation(Integer.parseInt(differYear), yearHolidaysMation);
                if (!ObjectUtils.isEmpty(yearMation)) {
                    String yearHour = yearMation.get("yearHour").toString();
                    // 获取每个季度应该相加的年假小时
                    String quarterYearHour = CalculationUtil.divide(yearHour, "4", 2);
                    annualLeave = CalculationUtil.add(annualLeave, quarterYearHour, 2);
                    LOGGER.info("annualLeaveStatistics calc staffId is: {} quarterYearHour is: {}", staffId, annualLeave);
                    // 更新员工年假信息
                    sysEveUserStaffService.updateStaffAnnualLeave(staffId, annualLeave, DateUtil.getTimeAndToString());
                }
            }
        } catch (Exception e) {
            LOGGER.warn("AnnualLeaveStatisticsQuartz error.", e);
        }
    }

    /**
     * 筛选已工作年份应该获取的年假信息
     *
     * @param differYear         已工作年份
     * @param yearHolidaysMation 年假信息
     * @return
     */
    private Map<String, Object> getConcertWithYearMation(int differYear, List<Map<String, Object>> yearHolidaysMation) {
        for (WinterVacationType q : WinterVacationType.values()) {
            if (q.getMin() <= differYear && differYear < q.getMax()) {
                List<Map<String, Object>> fillterMation = yearHolidaysMation.stream()
                    .filter(bean -> bean.get("yearType").toString().equals(q.getKey().toString()))
                    .collect(Collectors.toList());
                if (fillterMation == null || fillterMation.isEmpty()) {
                    return null;
                }
                return fillterMation.get(0);
            }
        }
        return null;
    }

    /**
     * 计算多少年，小数点后面的全部舍去
     *
     * @param differDays 相差的天数
     * @return
     */
    private String getDifferYear(int differDays) {
        String year = CalculationUtil.divide(String.valueOf(differDays), "365", 2);
        if (ToolUtil.isBlank(year)) {
            return "0";
        }
        return year.split("\\.")[0];
    }

    /**
     * 获取年假信息
     *
     * @return
     */
    private List<Map<String, Object>> getSystemYearHolidaysMation() {
        Map<String, Object> sysSetting = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        String yearHolidaysMationStr = sysSetting.get("yearHolidaysMation").toString();
        return JSONUtil.toList(yearHolidaysMationStr, null);
    }

}
