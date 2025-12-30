/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.centerrest.wages;

import com.skyeye.common.client.ClientConfiguration;
import com.skyeye.eve.centerrest.entity.wages.WagesStaffWorkTimeMationRest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: WagesStaffMationService
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 22:54
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface WagesStaffMationService {

    /**
     * 获取应出勤的班次以及小时
     *
     * @param wagesStaffWorkTimeMationRest 获取应出勤的班次以及小时的实体类
     * @return
     */
    @PostMapping("/setLastMonthBe")
    String setLastMonthBe(WagesStaffWorkTimeMationRest wagesStaffWorkTimeMationRest);

}
