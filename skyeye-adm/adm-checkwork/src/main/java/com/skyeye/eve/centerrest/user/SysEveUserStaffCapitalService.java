/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.centerrest.user;

import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @ClassName: SysEveUserStaffCapitalService
 * @Description: 员工非工资型的额外资金结算池接口服务类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 22:27
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface SysEveUserStaffCapitalService {

    /**
     * 新增员工待结算资金池信息
     *
     * @param params 员工待结算资金池信息，包括：员工id(staffId)，企业id(companyId)，部门id(departmentId)，指定年月(monthTime)，格式为：yyyy-MM，该资金来源类型(type)，金额(money)
     * @return 用户信息
     */
    @PostMapping("/addMonthMoney2StaffCapital")
    String addMonthMoney2StaffCapital(Map<String, Object> params);

}
