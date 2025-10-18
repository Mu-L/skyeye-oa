/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.IsUsedEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.lifecycle.dao.LifecycleStateDao;
import com.skyeye.lifecycle.entity.LifecycleState;
import com.skyeye.lifecycle.service.LifecycleStateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: LifecycleStateServiceImpl
 * @Description: 生命周期状态管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/3 20:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "生命周期状态管理", groupName = "生命周期管理", tenant = TenantEnum.WEAK_ISOLATION)
public class LifecycleStateServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleStateDao, LifecycleState> implements LifecycleStateService {

    @Override
    protected void validatorEntity(LifecycleState entity) {
        super.validatorEntity(entity);
        QueryWrapper<LifecycleState> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(LifecycleState::getNumCode), entity.getNumCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        LifecycleState lifecycleState = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(lifecycleState)) {
            throw new CustomException("名称或编码已存在.");
        }
    }

    @Override
    protected void createPrepose(LifecycleState entity) {
        entity.setIsUsed(IsUsedEnum.NOT_USED.getKey());
    }

    @Override
    protected void createPrepose(List<LifecycleState> entity) {
        entity.forEach(lifecycleState -> {
            lifecycleState.setIsUsed(IsUsedEnum.NOT_USED.getKey());
        });
    }

    @Override
    public void deletePreExecution(LifecycleState entity) {
        if (entity.getIsUsed() == IsUsedEnum.IN_USE.getKey()) {
            throw new CustomException("该商品已被使用，不能删除.");
        }
    }

    @Override
    public void queryLifecycleStateList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        String enabled = params.get("enabled").toString();

        QueryWrapper<LifecycleState> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getClassName), className);
        wrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getAppId), appId);
        if (StrUtil.isNotEmpty(enabled)) {
            wrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getEnabled), enabled);
        }
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(LifecycleState::getCreateTime));
        List<LifecycleState> lifecycleStateList = list(wrapper);
        iAuthUserService.setName(lifecycleStateList, "createId", "createName");
        iAuthUserService.setName(lifecycleStateList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(lifecycleStateList);
        outputObject.settotal(lifecycleStateList.size());
    }

    @Override
    public void setUsed(String id) {
        LifecycleState lifecycleState = super.selectById(id);
        if (lifecycleState.getIsUsed() == null || lifecycleState.getIsUsed() == IsUsedEnum.NOT_USED.getKey()) {
            // 更新生命周期状态为已被使用
            UpdateWrapper<LifecycleState> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(LifecycleState::getIsUsed), IsUsedEnum.IN_USE.getKey());
            update(updateWrapper);
            refreshCache(id);
        }
    }

    @Override
    public void setUsed(List<String> ids) {
        List<LifecycleState> lifecycleStateList = super.selectByIds(ids.toArray(new String[]{}));
        if (CollectionUtil.isEmpty(lifecycleStateList)) {
            return;
        }
        // 过滤出未被使用的生命周期状态
        List<String> idsList = lifecycleStateList.stream()
            .filter(lifecycleState -> lifecycleState.getIsUsed() == IsUsedEnum.NOT_USED.getKey())
            .map(LifecycleState::getId)
            .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idsList)) {
            return;
        }
        // 更新生命周期状态为已被使用
        UpdateWrapper<LifecycleState> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(MybatisPlusUtil.toColumns(LifecycleState::getId), idsList);
        updateWrapper.set(MybatisPlusUtil.toColumns(LifecycleState::getIsUsed), IsUsedEnum.IN_USE.getKey());
        update(updateWrapper);
        refreshCache(idsList);
    }

    @Override
    public void addLifecycleStateGroup(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();

        // 查询该类下已存在的生命周期状态
        QueryWrapper<LifecycleState> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getClassName), className);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getAppId), appId);
        List<LifecycleState> lifecycleStateList = list(queryWrapper);
        List<String> numCodeList = lifecycleStateList.stream().map(LifecycleState::getNumCode).collect(Collectors.toList());

        List<LifecycleState> saveList = new ArrayList<>();
        // 过滤掉已存在的生命周期状态
        for (FlowableStateEnum value : FlowableStateEnum.values()) {
            if (numCodeList.contains(value.getKey())) {
                continue;
            }
            LifecycleState lifecycleState = new LifecycleState();
            lifecycleState.setClassName(className);
            lifecycleState.setAppId(appId);
            lifecycleState.setName(value.getValue());
            lifecycleState.setNumCode(value.getKey());
            lifecycleState.setEnabled(EnableEnum.ENABLE_USING.getKey());
            lifecycleState.setColor(value.getColor());
            saveList.add(lifecycleState);
        }
        if (CollectionUtil.isEmpty(saveList)) {
            return;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(saveList, userId);
    }

    @Override
    public void removeAllLifecycleState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        QueryWrapper<LifecycleState> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getClassName), className);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleState::getAppId), appId);
        List<LifecycleState> lifecycleStateList = list(queryWrapper);
        if (CollectionUtil.isEmpty(lifecycleStateList)) {
            return;
        }
        List<String> idsList = lifecycleStateList.stream().map(LifecycleState::getId).collect(Collectors.toList());
        deleteById(idsList);
    }
}
