/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantAppMenuDao;
import com.skyeye.tenant.entity.TenantAppMenu;
import com.skyeye.tenant.service.TenantAppMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantAppMenuServiceImpl
 * @Description: 应用与菜单的关系管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "应用与菜单的关系管理", groupName = "租户管理", manageShow = false)
public class TenantAppMenuServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppMenuDao, TenantAppMenu> implements TenantAppMenuService {

    @Override
    public void saveList(String appId, Integer type, List<TenantAppMenu> beans) {
        deleteByAppId(appId, type);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (TenantAppMenu tenantAppMenu : beans) {
                tenantAppMenu.setAppId(appId);
                tenantAppMenu.setType(type);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByAppId(String appId, Integer type) {
        QueryWrapper<TenantAppMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppMenu::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppMenu::getType), type);
        remove(queryWrapper);
    }

    @Override
    public void deleteByAppId(String appId) {
        QueryWrapper<TenantAppMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppMenu::getAppId), appId);
        remove(queryWrapper);
    }

    @Override
    public List<TenantAppMenu> selectByAppId(String appId, Integer type) {
        QueryWrapper<TenantAppMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppMenu::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppMenu::getType), type);
        List<TenantAppMenu> list = list(queryWrapper);
        return list;
    }

    @Override
    public List<String> selectObjectIdsByAppId(String appId, Integer type) {
        List<TenantAppMenu> list = selectByAppId(appId, type);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.stream().map(TenantAppMenu::getObjectId).collect(Collectors.toList());
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public List<String> selectObjectIdsByAppId(List<String> appId, Integer type) {
        QueryWrapper<TenantAppMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantAppMenu::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantAppMenu::getType), type);
        List<TenantAppMenu> list = list(queryWrapper);
        return list.stream().map(TenantAppMenu::getObjectId).collect(Collectors.toList());
    }
}
