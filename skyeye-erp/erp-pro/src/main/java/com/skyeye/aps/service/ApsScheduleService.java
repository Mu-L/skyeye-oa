/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.aps.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * APS排程服务
 * 支持：多加工单、多工序、工序依赖、车间产能日历、标准工时
 *
 * @author: skyeye云系列--卫志强
 * @date: 2026/2/14
 */
public interface ApsScheduleService {

    /**
     * 执行排程计算，按产能日历分配计划时间，返回排产结果供界面展示。
     * 不保存到数据库，用户可微调后调用 saveSchedule 保存。
     * 计算时考虑：车间安排(farmIds)、历史已排单产能预占(优先执行)。
     */
    void schedule(InputObject inputObject, OutputObject outputObject);

    /**
     * 保存排产信息，将用户微调后的计划时间写入 MachinProcedure
     */
    void saveSchedule(InputObject inputObject, OutputObject outputObject);
}
