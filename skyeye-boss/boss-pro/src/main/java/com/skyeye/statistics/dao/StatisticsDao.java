/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.statistics.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: StatisticsDao
 * @Description: BOSS统计模块数据层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/3 15:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Mapper
public interface StatisticsDao extends SkyeyeBaseMapper<Object> {

    @IgnoreTenant
    Map<String, Object> getRecruitmentOverview(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> getIntervieweeStatusDistribution(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> getPersonRequireCompletion(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> getRecruitmentChannelEffect(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> getDepartmentRecruitmentStats(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> getMonthlyRecruitmentTrend(Map<String, Object> params);

    @IgnoreTenant
    Map<String, Object> getRegularAndQuitStats(Map<String, Object> params);

} 