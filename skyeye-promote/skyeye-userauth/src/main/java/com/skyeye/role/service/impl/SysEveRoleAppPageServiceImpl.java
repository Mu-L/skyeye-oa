/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.role.dao.SysEveRoleAppPageDao;
import com.skyeye.role.entity.SysEveRoleAppPage;
import com.skyeye.role.service.SysEveRoleAppPageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveRoleAppPageServiceImpl
 * @Description: 角色与桌面/菜单(移动端)关联表业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 10:55
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "角色与桌面/菜单(移动端)", groupName = "角色管理")
public class SysEveRoleAppPageServiceImpl extends SkyeyeBusinessServiceImpl<SysEveRoleAppPageDao, SysEveRoleAppPage> implements SysEveRoleAppPageService {

    @Override
    public void deleteByRoleId(String roleId) {
        QueryWrapper<SysEveRoleAppPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveRoleAppPage::getRoleId), roleId);
        remove(queryWrapper);
    }

    @Override
    public void createRoleAppPage(String roleId, List<String> appPageIds) {
        deleteByRoleId(roleId);
        if (CollectionUtil.isEmpty(appPageIds)) {
            return;
        }
        List<SysEveRoleAppPage> roleAppPageList = new ArrayList<>();
        for (String appPageId : appPageIds) {
            SysEveRoleAppPage roleAppPage = new SysEveRoleAppPage();
            roleAppPage.setRoleId(roleId);
            roleAppPage.setPageId(appPageId);
            roleAppPageList.add(roleAppPage);
        }
        createEntity(roleAppPageList, StrUtil.EMPTY);
    }

    @Override
    public List<String> querySysRoleAppPageIdByRoleId(String roleId) {
        QueryWrapper<SysEveRoleAppPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveRoleAppPage::getRoleId), roleId);
        List<SysEveRoleAppPage> roleAppPageList = list(queryWrapper);
        if (CollectionUtil.isEmpty(roleAppPageList)) {
            return new ArrayList<>();
        }
        return roleAppPageList.stream().map(SysEveRoleAppPage::getPageId).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> querySysRoleAppPageIdByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isNotEmpty(roleIds)) {
            QueryWrapper<SysEveRoleAppPage> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(MybatisPlusUtil.toColumns(SysEveRoleAppPage::getRoleId), roleIds);
            List<SysEveRoleAppPage> roleAppPageList = list(queryWrapper);
            if (CollectionUtil.isNotEmpty(roleAppPageList)) {
                return roleAppPageList.stream().collect(Collectors.groupingBy(SysEveRoleAppPage::getRoleId,
                    Collectors.mapping(SysEveRoleAppPage::getPageId, Collectors.toList())));
            }
        }
        return new HashMap<>();
    }
}
