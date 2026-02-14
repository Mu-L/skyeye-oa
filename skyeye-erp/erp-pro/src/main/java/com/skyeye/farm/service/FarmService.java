/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 */

package com.skyeye.farm.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.farm.entity.Farm;

import java.util.List;

/**
 * @ClassName: FarmService
 * @Description: 车间管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:47
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface FarmService extends SkyeyeBusinessService<Farm> {

    List<String> queryFarmIdListByDepartmentId(String departmentId);

    List<Farm> queryFarmListByChargePerson(String userId);

    void queryMyChargeFarmList(InputObject inputObject, OutputObject outputObject);

    void queryEnabledFarmList(InputObject inputObject, OutputObject outputObject);

    List<Farm> queryEnabledFarmList();

    List<Farm> queryFarmListByIds(List<String> farmIds);

    /**
     * 获取车间每日可用工时(分钟)，用于APS排产。
     * 未配置时返回默认值。
     */
    int getDailyWorkMinutes(String farmId);

    /**
     * 根据日期获取车间当日可用工时(分钟)。
     * @param farmId 车间ID
     * @param dateStr 日期字符串，格式：yyyy-MM-dd，为空时使用车间默认值
     */
    int getDailyWorkMinutes(String farmId, String dateStr);

}
