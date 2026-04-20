/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserOperLog;

public interface SysEveUserOperLogService extends SkyeyeBusinessService<SysEveUserOperLog> {

    /**
     * 清理指定保留月数之前的操作日志（分批删除）。
     *
     * @param batchSize    单批删除数量
     * @param retainMonths 保留最近多少个月日志
     * @return 本批清理删除数量
     */
    int cleanExpiredOperationLogs(int batchSize, int retainMonths);

    /**
     * 操作日志概览统计（总量、成功量、失败量、成功率、平均耗时、今日日志量）
     */
    void queryOperLogOverviewStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志按天趋势统计
     */
    void queryOperLogTrendStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志 Top 接口统计
     */
    void queryOperLogTopApiStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志 Top 请求路径统计
     */
    void queryOperLogTopPathStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志 Top 访问人统计
     */
    void queryOperLogTopUserStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志 Top 来源服务统计
     */
    void queryOperLogTopSourceServiceStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志 失败最多接口统计
     */
    void queryOperLogTopFailApiStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 操作日志 慢接口统计（按平均耗时）
     */
    void queryOperLogTopSlowApiStat(InputObject inputObject, OutputObject outputObject);

}
