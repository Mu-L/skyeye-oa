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
import com.skyeye.lifecycle.dao.LifecycleTemplateNodeDao;
import com.skyeye.lifecycle.entity.LifecycleTemplateNode;
import com.skyeye.lifecycle.entity.LifecycleTemplateNodeData;
import com.skyeye.lifecycle.service.LifecycleTemplateNodeDataService;
import com.skyeye.lifecycle.service.LifecycleTemplateNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LifecycleTemplateNodeServiceImpl
 * @Description: 生命周期模板节点管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 14:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板节点管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateNodeServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateNodeDao, LifecycleTemplateNode> implements LifecycleTemplateNodeService {

    @Autowired
    private LifecycleTemplateNodeDataService lifecycleTemplateNodeDataService;

    @Override
    public void saveList(String templateId, List<LifecycleTemplateNode> beans) {
        deleteByTemplateId(templateId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (LifecycleTemplateNode lifecycleTemplateNode : beans) {
                lifecycleTemplateNode.setTemplateId(templateId);
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(beans, userId);
            // 保存节点数据
            List<LifecycleTemplateNodeData> lifecycleTemplateNodeDataList = beans.stream()
                .filter(bean -> ObjectUtil.isNotEmpty(bean.getData()))
                .map(bean -> {
                    LifecycleTemplateNodeData lifecycleTemplateNodeData = bean.getData();
                    lifecycleTemplateNodeData.setNodeId(bean.getNodeId());
                    return lifecycleTemplateNodeData;
                }).collect(Collectors.toList());
            lifecycleTemplateNodeDataService.saveList(templateId, lifecycleTemplateNodeDataList);
        }
    }

    @Override
    public void deleteByTemplateId(String templateId) {
        QueryWrapper<LifecycleTemplateNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateNode::getTemplateId), templateId);
        remove(queryWrapper);
        lifecycleTemplateNodeDataService.deleteByTemplateId(templateId);
    }

    @Override
    public List<LifecycleTemplateNode> selectByTemplateId(String templateId) {
        QueryWrapper<LifecycleTemplateNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateNode::getTemplateId), templateId);
        List<LifecycleTemplateNode> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<String, LifecycleTemplateNodeData> lifecycleTemplateNodeDataMap = lifecycleTemplateNodeDataService.selectMapByTemplateId(templateId);
        for (LifecycleTemplateNode lifecycleTemplateNode : list) {
            LifecycleTemplateNodeData lifecycleTemplateNodeData = lifecycleTemplateNodeDataMap.get(lifecycleTemplateNode.getNodeId());
            if (ObjectUtil.isNotEmpty(lifecycleTemplateNodeData)) {
                lifecycleTemplateNode.setData(lifecycleTemplateNodeData);
            }
        }
        return list;
    }

    @Override
    public Map<String, List<LifecycleTemplateNode>> selectByTemplateId(List<String> templateId) {
        if (CollectionUtil.isEmpty(templateId)) {
            return Collections.emptyMap();
        }
        QueryWrapper<LifecycleTemplateNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LifecycleTemplateNode::getTemplateId), templateId);
        List<LifecycleTemplateNode> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Map<String, LifecycleTemplateNodeData>> dataMap = lifecycleTemplateNodeDataService.selectMapByTemplateId(templateId);
            for (LifecycleTemplateNode lifecycleTemplateNode : list) {
                Map<String, LifecycleTemplateNodeData> nodeDataMap = dataMap.get(lifecycleTemplateNode.getTemplateId());
                if (ObjectUtil.isNotEmpty(nodeDataMap)) {
                    LifecycleTemplateNodeData lifecycleTemplateNodeData = nodeDataMap.get(lifecycleTemplateNode.getNodeId());
                    if (ObjectUtil.isNotEmpty(lifecycleTemplateNodeData)) {
                        lifecycleTemplateNode.setData(lifecycleTemplateNodeData);
                    }
                }
            }
            return list.stream()
                .collect(Collectors.groupingBy(LifecycleTemplateNode::getTemplateId));
        }
        return Collections.emptyMap();
    }
}
