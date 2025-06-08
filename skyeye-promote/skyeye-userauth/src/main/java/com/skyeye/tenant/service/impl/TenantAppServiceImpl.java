/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.menu.dao.AppWorkPageDao;
import com.skyeye.menu.dao.SysEveMenuDao;
import com.skyeye.tenant.classenum.TenantAppMenuType;
import com.skyeye.tenant.dao.TenantAppDao;
import com.skyeye.tenant.entity.TenantApp;
import com.skyeye.tenant.entity.TenantAppMenu;
import com.skyeye.tenant.service.TenantAppMenuService;
import com.skyeye.tenant.service.TenantAppService;
import com.skyeye.win.service.SysEveDesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantAppServiceImpl
 * @Description: 租户应用管理服务层--平台租户
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "租户应用管理", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class TenantAppServiceImpl extends SkyeyeBusinessServiceImpl<TenantAppDao, TenantApp> implements TenantAppService {

    @Autowired
    private TenantAppMenuService tenantAppMenuService;

    @Autowired
    private SysEveDesktopService sysEveDesktopService;

    @Autowired
    private SysEveMenuDao sysEveMenuDao;

    @Autowired
    private AppWorkPageDao appWorkPageDao;

    @Override
    public TenantApp getDataFromDb(String id) {
        TenantApp tenantApp = super.getDataFromDb(id);
        List<String> menuIds = tenantAppMenuService.selectObjectIdsByAppId(id, TenantAppMenuType.PC.getKey());
        List<String> appMenuIds = tenantAppMenuService.selectObjectIdsByAppId(id, TenantAppMenuType.APP.getKey());
        tenantApp.setMenuIds(menuIds);
        tenantApp.setAppMenuIds(appMenuIds);
        return tenantApp;
    }

    @Override
    public void deletePostpose(String id) {
        tenantAppMenuService.deleteByAppId(id);
    }

    @Override
    @IgnoreTenant
    public void queryTenantAppBandMenuList(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new IllegalArgumentException("租户功能未开启");
        }
        String tenantId = TenantContext.getTenantId();
        if (!StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
            throw new IllegalArgumentException("非平台租户不能访问");
        }
        List<Map<String, Object>> beans = sysEveMenuDao.queryAllMenuList();
        // 获取桌面信息
        List<Map<String, Object>> desktopList = sysEveDesktopService.queryAllDataForMap();
        beans.addAll(desktopList);

        for (Map<String, Object> bean : beans) {
            String[] str = bean.get("pId").toString().split(",");
            bean.put("pId", str[str.length - 1]);
        }
        outputObject.setBeans(beans);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editTenantAppPCAuth(InputObject inputObject, OutputObject outputObject) {
        TenantApp tenantApp = inputObject.getParams(TenantApp.class);
        List<TenantAppMenu> beans = tenantApp.getMenuIds().stream().map(menuId -> {
            TenantAppMenu tenantAppMenu = new TenantAppMenu();
            tenantAppMenu.setObjectId(menuId);
            return tenantAppMenu;
        }).collect(Collectors.toList());
        tenantAppMenuService.saveList(tenantApp.getId(), TenantAppMenuType.PC.getKey(), beans);
        refreshCache(tenantApp.getId());
    }

    @Override
    public void queryTenantAppBandAppMenuList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = appWorkPageDao.queryAllAppMenuList();
        // 获取桌面信息
        List<Map<String, Object>> desktopList = sysEveDesktopService.queryAllDataForMap();
        beans.addAll(desktopList);
        outputObject.setBeans(beans);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editTenantAppAppMenuById(InputObject inputObject, OutputObject outputObject) {
        TenantApp tenantApp = inputObject.getParams(TenantApp.class);
        List<TenantAppMenu> beans = tenantApp.getAppMenuIds().stream().map(appMenuId -> {
            TenantAppMenu tenantAppMenu = new TenantAppMenu();
            tenantAppMenu.setObjectId(appMenuId);
            return tenantAppMenu;
        }).collect(Collectors.toList());
        tenantAppMenuService.saveList(tenantApp.getId(), TenantAppMenuType.APP.getKey(), beans);
        refreshCache(tenantApp.getId());
    }

    @Override
    public void queryAllTenantAppList(InputObject inputObject, OutputObject outputObject) {
        List<TenantApp> tenantApps = list();
        outputObject.setBeans(tenantApps);
        outputObject.settotal(tenantApps.size());
    }

    @Override
    public Map<String, TenantApp> queryTenantAppByAppId(String... appId) {
        List<String> appIdList = Arrays.asList(appId);
        if (CollectionUtil.isEmpty(appIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<TenantApp> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, appIdList);
        List<TenantApp> list = list(queryWrapper);
        return list.stream().collect(Collectors.toMap(TenantApp::getId, tenantApp -> tenantApp));
    }
}
