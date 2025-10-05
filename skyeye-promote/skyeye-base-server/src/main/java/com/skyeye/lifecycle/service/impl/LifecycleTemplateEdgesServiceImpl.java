/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.lifecycle.dao.LifecycleTemplateEdgesDao;
import com.skyeye.lifecycle.entity.LifecycleTemplateEdges;
import com.skyeye.lifecycle.entity.LifecycleTemplateEdgesData;
import com.skyeye.lifecycle.service.LifecycleTemplateEdgesDataService;
import com.skyeye.lifecycle.service.LifecycleTemplateEdgesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LifecycleTemplateEdgesServiceImpl
 * @Description: 生命周期模板连线业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 14:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板连线管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateEdgesServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateEdgesDao, LifecycleTemplateEdges> implements LifecycleTemplateEdgesService {

    @Autowired
    private LifecycleTemplateEdgesDataService lifecycleTemplateEdgesDataService;

    @Override
    public void saveList(String templateId, List<LifecycleTemplateEdges> beans) {
        deleteByTemplateId(templateId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (LifecycleTemplateEdges lifecycleTemplateEdges : beans) {
                lifecycleTemplateEdges.setTemplateId(templateId);
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(beans, userId);
            // 保存节点数据
            List<LifecycleTemplateEdgesData> lifecycleTemplateEdgesDataList = beans.stream()
                .filter(bean -> ObjectUtil.isNotEmpty(bean.getData()))
                .map(bean -> {
                    LifecycleTemplateEdgesData lifecycleTemplateEdgesData = bean.getData();
                    lifecycleTemplateEdgesData.setEdgesId(bean.getEdgesId());
                    return lifecycleTemplateEdgesData;
                }).collect(Collectors.toList());
            lifecycleTemplateEdgesDataService.saveList(templateId, lifecycleTemplateEdgesDataList);

        }
    }

    @Override
    public void deleteByTemplateId(String templateId) {
        QueryWrapper<LifecycleTemplateEdges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateEdges::getTemplateId), templateId);
        remove(queryWrapper);
        lifecycleTemplateEdgesDataService.deleteByTemplateId(templateId);
    }

    @Override
    public List<LifecycleTemplateEdges> selectByTemplateId(String templateId) {
        QueryWrapper<LifecycleTemplateEdges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateEdges::getTemplateId), templateId);
        List<LifecycleTemplateEdges> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<String, LifecycleTemplateEdgesData> lifecycleTemplateEdgesDataMap = lifecycleTemplateEdgesDataService.selectMapByTemplateId(templateId);
        for (LifecycleTemplateEdges lifecycleTemplateEdges : list) {
            LifecycleTemplateEdgesData lifecycleTemplateEdgesData = lifecycleTemplateEdgesDataMap.get(lifecycleTemplateEdges.getEdgesId());
            if (ObjectUtil.isNotEmpty(lifecycleTemplateEdgesData)) {
                lifecycleTemplateEdges.setData(lifecycleTemplateEdgesData);
            }
        }
        return list;
    }

    @Override
    public Map<String, List<LifecycleTemplateEdges>> selectByTemplateId(List<String> templateId) {
        if (CollectionUtil.isEmpty(templateId)) {
            return Collections.emptyMap();
        }
        QueryWrapper<LifecycleTemplateEdges> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LifecycleTemplateEdges::getTemplateId), templateId);
        List<LifecycleTemplateEdges> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Map<String, LifecycleTemplateEdgesData>> dataMap = lifecycleTemplateEdgesDataService.selectMapByTemplateId(templateId);
            for (LifecycleTemplateEdges lifecycleTemplateEdges : list) {
                Map<String, LifecycleTemplateEdgesData> edgesDataMap = dataMap.get(lifecycleTemplateEdges.getTemplateId());
                if (ObjectUtil.isNotEmpty(edgesDataMap)) {
                    LifecycleTemplateEdgesData lifecycleTemplateEdgesData = edgesDataMap.get(lifecycleTemplateEdges.getEdgesId());
                    if (ObjectUtil.isNotEmpty(lifecycleTemplateEdgesData)) {
                        lifecycleTemplateEdges.setData(lifecycleTemplateEdgesData);
                    }
                }
            }
            return list.stream()
                .collect(Collectors.groupingBy(LifecycleTemplateEdges::getTemplateId));
        }
        return Collections.emptyMap();
    }
}
