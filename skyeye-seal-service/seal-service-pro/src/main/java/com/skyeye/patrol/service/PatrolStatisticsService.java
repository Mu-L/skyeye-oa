/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: PatrolStatisticsService
 * @Description: 巡检统计服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PatrolStatisticsService {

    /**
     * 任务完成情况统计（总数、已完成、进行中、超时、取消）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryTaskCompletionStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 异常情况统计（正常/异常记录数量及占比）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryAbnormalStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按班组统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryStatsByTeam(InputObject inputObject, OutputObject outputObject);

    /**
     * 按点位统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryStatsByPoint(InputObject inputObject, OutputObject outputObject);

    /**
     * 按项目统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryStatsByItem(InputObject inputObject, OutputObject outputObject);

    /**
     * 按执行人统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryStatsByExecutor(InputObject inputObject, OutputObject outputObject);

    /**
     * 时间维度统计（日/周/月趋势）
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryTimeTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 完成率统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryCompletionRateStats(InputObject inputObject, OutputObject outputObject);

}

