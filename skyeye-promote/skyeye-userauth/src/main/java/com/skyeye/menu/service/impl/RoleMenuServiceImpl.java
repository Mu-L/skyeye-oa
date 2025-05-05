/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.menu.entity.AppWorkPage;
import com.skyeye.menu.entity.SysMenu;
import com.skyeye.menu.service.AppWorkPageService;
import com.skyeye.menu.service.RoleMenuService;
import com.skyeye.menu.service.SysEveMenuService;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.SysEveRoleService;
import com.skyeye.win.entity.SysDesktop;
import com.skyeye.win.service.SysEveDesktopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: RoleMenuServiceImpl
 * @Description: 角色菜单服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 9:39
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private SysEveRoleService sysEveRoleService;

    @Autowired
    private SysEveMenuService sysEveMenuService;

    @Autowired
    private AppWorkPageService appWorkPageService;

    @Autowired
    private SysEveDesktopService sysEveDesktopService;

    /**
     * 根据角色ID(逗号隔开的字符串)获取该角色拥有的菜单列表
     *
     * @param roleIds       角色id(逗号隔开的字符串)
     * @param userIdAndType userIdAndType
     * @return 该角色拥有的菜单列表
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> getRoleHasMenuListByRoleIds(String roleIds, String userIdAndType) {
        List<Map<String, Object>> menuResult = new ArrayList<>();
        if (ToolUtil.isBlank(roleIds)) {
            return menuResult;
        }
        List<String> roleIdList = Arrays.asList(roleIds.split(CommonCharConstants.COMMA_MARK)).stream()
            .filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (userIdAndType.lastIndexOf(SysUserAuthConstants.APP_IDENTIFYING) < 0) {
            // PC端
            menuResult = this.getRoleHasMenuListByRoleId(roleIdList, userIdAndType);
        } else {
            // 手机端
            menuResult = this.getRoleHasAPPMenuListByRoleId(roleIdList, userIdAndType);
        }
        // 去重
        menuResult = menuResult.stream().collect(
            Collectors.collectingAndThen(Collectors.toCollection(
                () -> new TreeSet<>(Comparator.comparing(m -> m.get("id").toString()))), ArrayList::new));
        // 排序
        menuResult = menuResult.stream()
            .sorted(Comparator.comparingInt(RoleMenuServiceImpl::comparingByOrderNum))
            .collect(Collectors.toList());
        // 转成树结构
        if (!CollectionUtil.isEmpty(menuResult)) {
            if (userIdAndType.lastIndexOf(SysUserAuthConstants.APP_IDENTIFYING) < 0) {
                // PC端
                menuResult = ToolUtil.listToTree(menuResult, "id", "parentId", "childs");
            } else {
                // 手机端
                menuResult = ToolUtil.listToTree(menuResult, "id", "pId", "children");
            }
        }
        return menuResult;
    }

    private static Integer comparingByOrderNum(Map<String, Object> map) {
        return (Integer) map.get("orderNum");
    }

    /**
     * 根据角色ID获取该角色拥有的菜单列表
     *
     * @param roleId 角色id
     * @return 该角色拥有的菜单列表
     * @throws Exception
     */
    private List<Map<String, Object>> getRoleHasMenuListByRoleId(List<String> roleId, String userIdAndType) {
        List<Role> roleList = sysEveRoleService.selectByIds(roleId.toArray(new String[]{}));
        if (CollectionUtil.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        // 获取menuId集合并合并去重
        List<String> menuIdList = roleList.stream().filter(role -> CollectionUtil.isNotEmpty(role.getMenuIds()))
            .map(Role::getMenuIds).flatMap(List::stream).distinct().collect(Collectors.toList());

        return queryMenuListByMenuIds(menuIdList, userIdAndType);
    }

    /**
     * 根据角色ID获取该角色拥有的菜单列表(手机端)
     *
     * @param roleId 角色id
     * @return 该角色拥有的菜单列表
     * @throws Exception
     */
    private List<Map<String, Object>> getRoleHasAPPMenuListByRoleId(List<String> roleId, String userIdAndType) {
        List<Role> roleList = sysEveRoleService.selectByIds(roleId.toArray(new String[]{}));
        if (CollectionUtil.isEmpty(roleList)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        // 获取menuId集合并合并去重
        List<String> menuIdList = roleList.stream().filter(role -> CollectionUtil.isNotEmpty(role.getAppMenuId()))
            .map(Role::getAppMenuId).flatMap(List::stream).distinct().collect(Collectors.toList());
        List<Map<String, Object>> menuList = queryMenuListByMenuIds(menuIdList, userIdAndType);
        if (CollectionUtil.isNotEmpty(menuList)) {
            result.addAll(menuList);
        }
        // 获取桌面
        List<String> desktopIdList = roleList.stream().filter(role -> CollectionUtil.isNotEmpty(role.getAppDesktopId()))
            .map(Role::getAppDesktopId).flatMap(List::stream).distinct().collect(Collectors.toList());
        List<SysDesktop> desktopEntityList = sysEveDesktopService.selectByIds(desktopIdList.toArray(new String[]{}));
        List<Map<String, Object>> desktopList = desktopEntityList.stream().map(desktop -> {
            Map<String, Object> desktopMapResult = BeanUtil.beanToMap(desktop);
            desktopMapResult.put("pId", CommonNumConstants.NUM_ZERO.toString());
            desktopMapResult.put("orderNum", desktop.getOrderBy());
            desktopMapResult.put("type", "desktop");
            return desktopMapResult;
        }).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(desktopList)) {
            result.addAll(desktopList);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> queryMenuListByMenuIds(List<String> menuIds, String userIdAndType) {
        List<Map<String, Object>> menuList;
        if (userIdAndType.lastIndexOf(SysUserAuthConstants.APP_IDENTIFYING) < 0) {
            // PC端--包含菜单
            List<SysMenu> menuEntityList = sysEveMenuService.selectByIds(menuIds.toArray(new String[]{}));
            menuList = menuEntityList.stream().map(menu -> {
                Map<String, Object> menuMapResult = BeanUtil.beanToMap(menu);
                menuMapResult.put("childs", null);
                menuMapResult.put("maxOpen", "-1");
                menuMapResult.put("extend", "false");
                if (menu.getSysWinMation() != null) {
                    menuMapResult.put("sysWinUrl", menu.getSysWinMation().getSysUrl());
                }
                return menuMapResult;
            }).collect(Collectors.toList());
        } else {
            // 手机端--包含菜单
            List<AppWorkPage> menuEntityList = appWorkPageService.selectByIds(menuIds.toArray(new String[]{}));
            menuList = menuEntityList.stream().map(menu -> {
                Map<String, Object> menuMapResult = BeanUtil.beanToMap(menu);
                if (StrUtil.equals(menu.getParentId(), CommonNumConstants.NUM_ZERO.toString())) {
                    menuMapResult.put("pId", menu.getDesktopId());
                } else {
                    menuMapResult.put("pId", menu.getParentId());
                }
                menuMapResult.put("orderNum", menu.getOrderBy());
                menuMapResult.put("type", "page");
                return menuMapResult;
            }).collect(Collectors.toList());
        }
        for (Map<String, Object> authPoint : menuList) {
            authPoint.remove("createId");
            authPoint.remove("createTime");
            authPoint.remove("lastUpdateId");
            authPoint.remove("lastUpdateTime");
            authPoint.remove("serviceClassName");
        }

        return menuList;
    }

}
