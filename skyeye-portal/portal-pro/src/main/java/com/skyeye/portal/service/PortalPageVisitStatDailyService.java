/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.portal.entity.PortalPageVisitStatDaily;

import java.util.List;
import java.util.Map;

/**
 * 官网页面访问日 PV 汇总服务接口（内部服务，manageShow = false）
 */
public interface PortalPageVisitStatDailyService extends SkyeyeBusinessService<PortalPageVisitStatDaily> {

    /**
     * 累加指定日期、页面路径的 PV；不存在则新增一条 pv_count = 1 的记录
     *
     * @param statDate   统计日期 yyyy-MM-dd
     * @param pagePath   页面路径
     * @param pageName   页面名称（可为空）
     * @param createTime 创建时间
     */
    void incrPagePv(String statDate, String pagePath, String pageName, String createTime);

    /**
     * 统计日期区间内全站 PV 总和
     */
    long sumPvByDateRange(String fromDate, String toDate);

    /**
     * 统计指定日期的全站 PV 总和
     */
    long sumPvByStatDate(String statDate);

    /**
     * 按天聚合 PV，返回 statDate、pvCount
     */
    List<Map<String, Object>> queryDailyPvTrend(String fromDate, String toDate);

    /**
     * 按页面路径聚合 PV 并取 TopN，返回 pagePath、pageName、totalCount
     */
    List<Map<String, Object>> queryTopPagePv(String fromDate, String toDate, int topN);
}
