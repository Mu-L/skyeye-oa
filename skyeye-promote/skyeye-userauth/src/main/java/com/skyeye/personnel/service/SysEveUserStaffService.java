/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserStaff;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveUserStaffService
 * @Description: 员工管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/18 11:50
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserStaffService extends SkyeyeBusinessService<SysEveUserStaff> {

    void editSysUserStaffState(InputObject inputObject, OutputObject outputObject);

    void updateStaffType(String id, Integer type);

    void querySysUserStaffLogin(InputObject inputObject, OutputObject outputObject);

    void queryUserMationList(InputObject inputObject, OutputObject outputObject);

    /**
     * 根据用户ids/员工ids获取员工信息集合
     *
     * @param userIds  用户id，多个逗号隔离(两个参数传一个即可，默认优先以userIds查询为主)
     * @param staffIds 员工id，多个逗号隔开(两个参数传一个即可，默认优先以userIds查询为主)
     * @return
     */
    List<Map<String, Object>> queryUserMationList(String userIds, String staffIds);

    void editSysUserStaffAnnualLeaveById(InputObject inputObject, OutputObject outputObject);

    void updateSysUserStaffHolidayNumberById(InputObject inputObject, OutputObject outputObject);

    void updateSysUserStaffRetiredHolidayNumberById(InputObject inputObject, OutputObject outputObject);

    void queryStaffCheckWorkTimeRelationNameByStaffId(InputObject inputObject, OutputObject outputObject);

    void editSysUserStaffBindUserId(String staffId, String userId);

    void queryAllSysUserIsIncumbency(InputObject inputObject, OutputObject outputObject);

    void editSysUserStaffActMoneyById(InputObject inputObject, OutputObject outputObject);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return boolean 是否存在, true:存在, false:不存在
     */
    boolean checkPhoneExists(String phone);

    void querySysUserStaffByUserId(InputObject inputObject, OutputObject outputObject);
}
