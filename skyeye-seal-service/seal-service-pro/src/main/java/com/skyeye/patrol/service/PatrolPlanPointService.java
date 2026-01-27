/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.patrol.entity.PatrolPlanPoint;

import java.util.List;

/**
 * @ClassName: PatrolPlanPointService
 * @Description: 巡检计划点位关联服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PatrolPlanPointService extends SkyeyeBusinessService<PatrolPlanPoint> {

    /**
     * 根据计划ID删除所有关联的点位
     *
     * @param planId 计划ID
     */
    void deleteByParentId(String planId);

    /**
     * 根据计划ID查询关联的点位ID列表
     *
     * @param planId 计划ID
     * @return 点位ID列表
     */
    List<String> selectByParentId(String planId);

    /**
     * 根据计划ID列表批量查询关联的点位ID列表
     *
     * @param planIds 计划ID列表
     * @return Map<计划ID, 点位ID列表>
     */
    java.util.Map<String, List<String>> selectMapByParentId(List<String> planIds);

    /**
     * 保存计划关联的点位列表
     *
     * @param planId 计划ID
     * @param pointIds 点位ID列表
     */
    void saveList(String planId, List<String> pointIds);

}

