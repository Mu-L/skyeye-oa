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
import com.skyeye.patrol.dao.PatrolPlanItemDao;
import com.skyeye.patrol.entity.PatrolPlanItem;
import com.skyeye.patrol.service.PatrolPlanItemService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolPlanItemServiceImpl
 * @Description: 巡检计划项目关联服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检计划项目关联", groupName = "巡检计划项目关联", manageShow = false)
public class PatrolPlanItemServiceImpl extends SkyeyeBusinessServiceImpl<PatrolPlanItemDao, PatrolPlanItem> implements PatrolPlanItemService {

    @Override
    public void deleteByParentId(String planId) {
        if (StrUtil.isEmpty(planId)) {
            return;
        }
        QueryWrapper<PatrolPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPlanItem::getPlanId), planId);
        remove(queryWrapper);
    }

    @Override
    public List<String> selectByParentId(String planId) {
        QueryWrapper<PatrolPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPlanItem::getPlanId), planId);
        List<PatrolPlanItem> list = list(queryWrapper);
        return list.stream().map(PatrolPlanItem::getItemId).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> selectMapByParentId(List<String> planIds) {
        if (CollectionUtil.isEmpty(planIds)) {
            return Collections.emptyMap();
        }
        QueryWrapper<PatrolPlanItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(PatrolPlanItem::getPlanId), planIds);
        List<PatrolPlanItem> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(
            PatrolPlanItem::getPlanId,
            Collectors.mapping(PatrolPlanItem::getItemId, Collectors.toList())
        ));
    }

    @Override
    public void saveList(String planId, List<String> itemIds) {
        deleteByParentId(planId);
        if (CollectionUtil.isNotEmpty(itemIds)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            List<PatrolPlanItem> planItemList = itemIds.stream().map(itemId -> {
                PatrolPlanItem planItem = new PatrolPlanItem();
                planItem.setPlanId(planId);
                planItem.setItemId(itemId);
                return planItem;
            }).collect(Collectors.toList());
            createEntity(planItemList, userId);
        }
    }
}

