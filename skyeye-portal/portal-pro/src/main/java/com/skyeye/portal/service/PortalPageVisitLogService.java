/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalPageVisitLog;

/**
 * 官网页面访问统计服务接口
 */
public interface PortalPageVisitLogService extends SkyeyeBusinessService<PortalPageVisitLog> {

    /**
     * 记录官网页面访问（官网匿名上报，同时写入明细与日汇总）
     */
    void recordPortalPageVisit(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询官网访问概览统计（读日汇总表）
     */
    void queryPortalVisitOverviewStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询官网访问每日趋势统计（读日汇总表）
     */
    void queryPortalVisitTrendStat(InputObject inputObject, OutputObject outputObject);

    /**
     * 查询官网热门页面统计（读日汇总表）
     */
    void queryPortalVisitTopPageStat(InputObject inputObject, OutputObject outputObject);
}
