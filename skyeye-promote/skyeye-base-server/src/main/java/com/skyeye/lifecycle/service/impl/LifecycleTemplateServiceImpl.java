/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.lifecycle.dao.LifecycleTemplateDao;
import com.skyeye.lifecycle.entity.*;
import com.skyeye.lifecycle.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LifecycleTemplateServiceImpl
 * @Description: 生命周期模板管理业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 11:27
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateDao, LifecycleTemplate> implements LifecycleTemplateService {

    @Autowired
    private LifecycleTemplateNodeService lifecycleTemplateNodeService;

    @Autowired
    private LifecycleTemplateEdgesService lifecycleTemplateEdgesService;

    @Autowired
    private LifecycleStateService lifecycleStateService;

    @Autowired
    private LifecycleTemplateMasterService lifecycleTemplateMasterService;

    @Override
    public QueryWrapper<LifecycleTemplate> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LifecycleTemplate> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getMasterId), commonPageInfo.getMasterId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LifecycleTemplate::getLargeVersion));
        return queryWrapper;
    }

    @Override
    protected void createPrepose(LifecycleTemplate entity) {
        if (StrUtil.isNotEmpty(entity.getId())) {
            // 如果id不为空，说明可能是创建新版本或者编辑已有版本，要清空缓存，防止创建新版本情况下，老版本数据缓存
            clearCache(entity.getId());
        }
    }

    @Override
    public String createEntity(LifecycleTemplate entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(LifecycleTemplate entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    protected void writePostpose(LifecycleTemplate entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存节点数据
        lifecycleTemplateNodeService.saveList(entity.getId(), entity.getNodes());
        // 保存连线数据
        lifecycleTemplateEdgesService.saveList(entity.getId(), entity.getEdges());
    }

    @Override
    public LifecycleTemplate getDataFromDb(String id) {
        LifecycleTemplate lifecycleTemplate = super.getDataFromDb(id);
        // 查询节点数据
        List<LifecycleTemplateNode> lifecycleTemplateNodes = lifecycleTemplateNodeService.selectByTemplateId(lifecycleTemplate.getId());
        // 查询连线数据
        List<LifecycleTemplateEdges> lifecycleTemplateEdges = lifecycleTemplateEdgesService.selectByTemplateId(lifecycleTemplate.getId());

        lifecycleTemplate.setNodes(lifecycleTemplateNodes);
        lifecycleTemplate.setEdges(lifecycleTemplateEdges);
        return lifecycleTemplate;
    }

    @Override
    protected List<LifecycleTemplate> getDataFromDb(List<String> idList) {
        List<LifecycleTemplate> lifecycleTemplateList = super.getDataFromDb(idList);
        // 查询节点数据
        Map<String, List<LifecycleTemplateNode>> nodeMap = lifecycleTemplateNodeService.selectByTemplateId(idList);
        // 查询连线数据
        Map<String, List<LifecycleTemplateEdges>> edgesMap = lifecycleTemplateEdgesService.selectByTemplateId(idList);

        for (LifecycleTemplate lifecycleTemplate : lifecycleTemplateList) {
            lifecycleTemplate.setNodes(nodeMap.get(lifecycleTemplate.getId()));
            lifecycleTemplate.setEdges(edgesMap.get(lifecycleTemplate.getId()));
        }
        return lifecycleTemplateList;
    }

    @Override
    public LifecycleTemplate selectById(String id) {
        LifecycleTemplate lifecycleTemplate = super.selectById(id);
        if (CollectionUtil.isNotEmpty(lifecycleTemplate.getNodes())) {
            // 查询节点状态信息
            List<String> stateIdList = lifecycleTemplate.getNodes().stream()
                .filter(lifecycleTemplateNode -> ObjectUtil.isNotEmpty(lifecycleTemplateNode.getData()))
                .map(lifecycleTemplateNode -> lifecycleTemplateNode.getData().getState())
                .filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(stateIdList)) {
                Map<String, LifecycleState> stateMap = lifecycleStateService.selectMapByIds(stateIdList);
                for (LifecycleTemplateNode lifecycleTemplateNode : lifecycleTemplate.getNodes()) {
                    if (ObjectUtil.isEmpty(lifecycleTemplateNode.getData())) {
                        continue;
                    }
                    String stateId = lifecycleTemplateNode.getData().getState();
                    if (StrUtil.isBlank(stateId)) {
                        continue;
                    }
                    LifecycleState lifecycleState = stateMap.get(stateId);
                    if (ObjectUtil.isEmpty(lifecycleState)) {
                        continue;
                    }
                    lifecycleTemplateNode.getData().setStateMation(lifecycleState);
                }
            }
        }
        lifecycleTemplateMasterService.setDataMation(lifecycleTemplate, LifecycleTemplate::getMasterId);
        return lifecycleTemplate;
    }

    @Override
    public List<LifecycleTemplate> selectByIds(String... ids) {
        List<LifecycleTemplate> lifecycleTemplates = super.selectByIds(ids);

        // 收集所有模板中的所有状态ID
        List<String> allStateIdList = lifecycleTemplates.stream()
            .filter(lifecycleTemplate -> CollectionUtil.isNotEmpty(lifecycleTemplate.getNodes()))
            .flatMap(lifecycleTemplate -> lifecycleTemplate.getNodes().stream())
            .filter(lifecycleTemplateNode -> ObjectUtil.isNotEmpty(lifecycleTemplateNode.getData()))
            .map(lifecycleTemplateNode -> lifecycleTemplateNode.getData().getState())
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());

        // 一次性批量查询所有状态信息
        Map<String, LifecycleState> stateMap = CollectionUtil.isNotEmpty(allStateIdList)
            ? lifecycleStateService.selectMapByIds(allStateIdList)
            : new HashMap<>();

        // 为每个模板的节点设置状态信息
        for (LifecycleTemplate lifecycleTemplate : lifecycleTemplates) {
            if (CollectionUtil.isEmpty(lifecycleTemplate.getNodes())) {
                continue;
            }
            for (LifecycleTemplateNode lifecycleTemplateNode : lifecycleTemplate.getNodes()) {
                if (ObjectUtil.isEmpty(lifecycleTemplateNode.getData())) {
                    continue;
                }
                String stateId = lifecycleTemplateNode.getData().getState();
                if (StrUtil.isBlank(stateId)) {
                    continue;
                }
                LifecycleState lifecycleState = stateMap.get(stateId);
                if (ObjectUtil.isEmpty(lifecycleState)) {
                    continue;
                }
                lifecycleTemplateNode.getData().setStateMation(lifecycleState);
            }
        }

        return lifecycleTemplates;
    }

    @Override
    protected void deletePostpose(LifecycleTemplate entity) {
        super.deletePostpose(entity);
        // 删除节点数据
        lifecycleTemplateNodeService.deleteByTemplateId(entity.getId());
        // 删除连线数据
        lifecycleTemplateEdgesService.deleteByTemplateId(entity.getId());
    }

    @Override
    public void queryCurrentLifecycleTemplateByMasterId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String masterId = params.get("masterId").toString();
        LifecycleTemplate lifecycleTemplate = querCurrentLifecycleTemplateByMasterId(masterId);
        outputObject.setBean(lifecycleTemplate);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    private LifecycleTemplate querCurrentLifecycleTemplateByMasterId(String masterId) {
        // 查询当前生效的生命周期模板
        QueryWrapper<LifecycleTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        // largeVersion最大得模板
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LifecycleTemplate::getLargeVersion));
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getMasterId), masterId);
        LifecycleTemplate lifecycleTemplate = getOne(queryWrapper, false);
        if (lifecycleTemplate == null) {
            return null;
        }
        lifecycleTemplate = selectById(lifecycleTemplate.getId());
        return lifecycleTemplate;
    }

    @Override
    public void queryCurrentLifecycleTemplateByAppIdAndClassName(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        LifecycleTemplateMaster lifecycleTemplateMaster = lifecycleTemplateMasterService.queryLifecycleTemplateMaster(appId, className);
        if (lifecycleTemplateMaster == null) {
            return;
        }
        LifecycleTemplate lifecycleTemplate = querCurrentLifecycleTemplateByMasterId(lifecycleTemplateMaster.getId());
        outputObject.setBean(lifecycleTemplate);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
