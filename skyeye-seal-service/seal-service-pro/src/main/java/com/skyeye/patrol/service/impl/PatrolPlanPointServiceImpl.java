/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.patrol.dao.PatrolPlanPointDao;
import com.skyeye.patrol.entity.PatrolPlanPoint;
import com.skyeye.patrol.service.PatrolPlanPointService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolPlanPointServiceImpl
 * @Description: 巡检计划点位关联服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检计划点位关联", groupName = "巡检计划点位关联", manageShow = false)
public class PatrolPlanPointServiceImpl extends SkyeyeBusinessServiceImpl<PatrolPlanPointDao, PatrolPlanPoint> implements PatrolPlanPointService {

    @Override
    public void deleteByParentId(String planId) {
        if (StrUtil.isEmpty(planId)) {
            return;
        }
        QueryWrapper<PatrolPlanPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPlanPoint::getPlanId), planId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String planId) {
        QueryWrapper<PatrolPlanPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPlanPoint::getPlanId), planId);
        List<PatrolPlanPoint> list = list(queryWrapper);
        return list.stream().map(PatrolPlanPoint::getPointId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> selectMapByParentId(List<String> planIds) {
        if (CollectionUtil.isEmpty(planIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<PatrolPlanPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(PatrolPlanPoint::getPlanId), planIds);
        List<PatrolPlanPoint> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(
            PatrolPlanPoint::getPlanId,
            Collectors.mapping(PatrolPlanPoint::getPointId, Collectors.toList())
        ));
    }

    @Override
    public void saveList(String planId, List<String> pointIds) {
        deleteByParentId(planId);
        if (CollectionUtil.isNotEmpty(pointIds)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            List<PatrolPlanPoint> planPointList = pointIds.stream().map(pointId -> {
                PatrolPlanPoint planPoint = new PatrolPlanPoint();
                planPoint.setPlanId(planId);
                planPoint.setPointId(pointId);
                return planPoint;
            }).collect(Collectors.toList());
            createEntity(planPointList, userId);
        }
    }
}

