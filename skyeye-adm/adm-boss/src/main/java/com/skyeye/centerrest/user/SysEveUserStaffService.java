/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.centerrest.user;

import com.skyeye.centerrest.entity.staff.UserStaffLeaveRest;
import com.skyeye.centerrest.entity.staff.UserStaffRest;
import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: SysEveUserStaffService
 * @Description: 员工信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/26 14:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface SysEveUserStaffService {

    /**
     * 新增员工信息
     *
     * @param userStaffRest 员工信息
     * @return
     */
    @PostMapping("/writeSysUserStaff")
    String insertNewUserStaff(UserStaffRest userStaffRest);

    /**
     * 员工离职
     *
     * @param userStaffLeaveRest 员工离职信息
     * @return
     */
    @PostMapping("/staff006")
    String userStaffQuit(UserStaffLeaveRest userStaffLeaveRest);

}
