/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.patrol.dao.PatrolPlanDao;
import com.skyeye.patrol.entity.PatrolItem;
import com.skyeye.patrol.entity.PatrolPlan;
import com.skyeye.patrol.entity.PatrolPoint;
import com.skyeye.patrol.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolPlanServiceImpl
 * @Description: 巡检计划服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检计划", groupName = "巡检计划")
public class PatrolPlanServiceImpl extends SkyeyeBusinessServiceImpl<PatrolPlanDao, PatrolPlan> implements PatrolPlanService {

    @Autowired
    private PatrolTeamService patrolTeamService;

    @Autowired
    private PatrolPointService patrolPointService;

    @Autowired
    private PatrolItemService patrolItemService;

    @Autowired
    private PatrolPlanPointService patrolPlanPointService;

    @Autowired
    private PatrolPlanItemService patrolPlanItemService;

    @Override
    protected QueryWrapper<PatrolPlan> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatrolPlan> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPlan::getEnabled), commonPageInfo.getEnabled());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPlan::getTeamId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public void createPrepose(PatrolPlan entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public void writePostpose(PatrolPlan entity, String userId) {
        // 保存关联的点位
        patrolPlanPointService.saveList(entity.getId(), entity.getPointIds());
        // 保存关联的项目
        patrolPlanItemService.saveList(entity.getId(), entity.getItemIds());
        super.writePostpose(entity, userId);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置班组信息
        patrolTeamService.setMationForMap(beans, "teamId", "teamMation");
        return beans;
    }

    @Override
    public PatrolPlan getDataFromDb(String id) {
        PatrolPlan patrolPlan = super.getDataFromDb(id);
        // 查询关联的点位ID列表
        patrolPlan.setPointIds(patrolPlanPointService.selectByParentId(id));
        // 查询关联的项目ID列表
        patrolPlan.setItemIds(patrolPlanItemService.selectByParentId(id));
        return patrolPlan;
    }

    @Override
    public List<PatrolPlan> getDataFromDb(List<String> idList) {
        List<PatrolPlan> planList = super.getDataFromDb(idList);
        if (CollectionUtil.isEmpty(planList)) {
            return planList;
        }
        List<String> planIdList = planList.stream().map(PatrolPlan::getId).collect(Collectors.toList());
        // 批量查询关联的点位ID列表
        Map<String, List<String>> pointIdMap = patrolPlanPointService.selectMapByParentId(planIdList);
        // 批量查询关联的项目ID列表
        Map<String, List<String>> itemIdMap = patrolPlanItemService.selectMapByParentId(planIdList);
        // 设置关联的点位ID和项目ID
        planList.forEach(plan -> {
            plan.setPointIds(pointIdMap.get(plan.getId()));
            plan.setItemIds(itemIdMap.get(plan.getId()));
        });
        return planList;
    }

    @Override
    public PatrolPlan selectById(String id) {
        PatrolPlan patrolPlan = super.selectById(id);
        if (patrolPlan == null) {
            return null;
        }
        // 设置班组信息
        patrolTeamService.setDataMation(patrolPlan, PatrolPlan::getTeamId);
        // 设置关联的点位信息
        if (CollectionUtil.isNotEmpty(patrolPlan.getPointIds())) {
            List<PatrolPoint> points = patrolPointService.selectByIds(patrolPlan.getPointIds().toArray(new String[]{}));
            patrolPlan.setPointMationList(points);
        }
        // 设置关联的项目信息
        if (CollectionUtil.isNotEmpty(patrolPlan.getItemIds())) {
            List<PatrolItem> items = patrolItemService.selectByIds(patrolPlan.getItemIds().toArray(new String[]{}));
            patrolPlan.setItemMationList(items);
        }
        return patrolPlan;
    }

    @Override
    protected void deletePostpose(PatrolPlan entity) {
        // 删除关联的点位
        patrolPlanPointService.deleteByParentId(entity.getId());
        // 删除关联的项目
        patrolPlanItemService.deleteByParentId(entity.getId());
    }

}

