/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.statistics.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: StatisticsService
 * @Description: BOSS统计模块服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/3 15:18
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface StatisticsService {

    void getRecruitmentOverview(InputObject inputObject, OutputObject outputObject);

    void getIntervieweeStatusDistribution(InputObject inputObject, OutputObject outputObject);

    void getPersonRequireCompletion(InputObject inputObject, OutputObject outputObject);

    void getRecruitmentChannelEffect(InputObject inputObject, OutputObject outputObject);

    void getDepartmentRecruitmentStats(InputObject inputObject, OutputObject outputObject);

    void getMonthlyRecruitmentTrend(InputObject inputObject, OutputObject outputObject);

    void getRegularAndQuitStats(InputObject inputObject, OutputObject outputObject);

}
