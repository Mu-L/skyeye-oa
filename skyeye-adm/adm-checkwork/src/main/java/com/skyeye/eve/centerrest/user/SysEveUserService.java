/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.centerrest.user;

import com.skyeye.common.client.ClientConfiguration;
import com.skyeye.eve.centerrest.entity.checkwork.UserStaffHolidayRest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: SysEveUserService
 * @Description: 用户信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/26 14:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface SysEveUserService {

    /**
     * 修改员工剩余年假信息
     *
     * @param sysUserStaffHoliday 员工年假信息
     * @return 用户信息
     */
    @PostMapping("/editSysUserStaffAnnualLeaveById")
    String editSysUserStaffAnnualLeaveById(UserStaffHolidayRest sysUserStaffHoliday);

    /**
     * 修改员工的补休池剩余补休信息
     *
     * @param sysUserStaffHoliday 员工年假信息
     * @return 用户信息
     */
    @PostMapping("/updateSysUserStaffHolidayNumberById")
    String updateSysUserStaffHolidayNumberById(UserStaffHolidayRest sysUserStaffHoliday);

    /**
     * 修改员工的补休池已休补休信息
     *
     * @param sysUserStaffHoliday 员工年假信息
     * @return 用户信息
     */
    @PostMapping("/updateSysUserStaffRetiredHolidayNumberById")
    String updateSysUserStaffRetiredHolidayNumberById(UserStaffHolidayRest sysUserStaffHoliday);

    /**
     * 根据员工id获取该员工的考勤时间段
     *
     * @param staffId 员工id
     * @return
     */
    @GetMapping("/queryStaffCheckWorkTimeRelationNameByStaffId")
    String queryStaffCheckWorkTimeRelationNameByStaffId(@RequestParam("staffId") String staffId);
}
