/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.portal.entity.PortalPageVisitUvDaily;

import java.util.List;
import java.util.Map;

/**
 * 官网页面访问日 UV 去重服务接口（内部服务，manageShow = false）
 */
public interface PortalPageVisitUvDailyService extends SkyeyeBusinessService<PortalPageVisitUvDaily> {

    /**
     * 记录当日访客（同一 statDate + visitorId 仅保留一条）
     *
     * @param statDate   统计日期 yyyy-MM-dd
     * @param visitorId  访客标识
     * @param createTime 创建时间
     */
    void recordDailyUv(String statDate, String visitorId, String createTime);

    /**
     * 统计指定日期的全站 UV（去重访客数）
     */
    long countUvByStatDate(String statDate);

    /**
     * 按天统计 UV，返回 statDate、uvCount
     */
    List<Map<String, Object>> queryDailyUvTrend(String fromDate, String toDate);
}
