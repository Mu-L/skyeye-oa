/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.checkwork;

import com.skyeye.common.client.ClientConfiguration;
import com.skyeye.rest.checkwork.entity.DayWorkMationRest;
import com.skyeye.rest.checkwork.entity.UserOtherDayMationRest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: CheckWorkService
 * @Description: 考勤模块接口信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/26 20:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-checkwork}", configuration = ClientConfiguration.class)
public interface CheckWorkService {

    /**
     * 获取指定天中的工作日
     *
     * @param dayWorkMationRest 入参信息
     */
    @PostMapping("/queryDayWorkMation")
    String queryDayWorkMation(DayWorkMationRest dayWorkMationRest);

    /**
     * 获取用户指定班次在指定月份的其他日期信息[审核通过的](例如：请假，出差，加班等)
     *
     * @param userOtherDayMationRest 入参信息
     */
    @PostMapping("/getUserOtherDayMation")
    String getUserOtherDayMation(UserOtherDayMationRest userOtherDayMationRest);

}
