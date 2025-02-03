/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.role.dao.SysEveRoleMenuDao;
import com.skyeye.role.entity.SysEveRoleMenu;
import com.skyeye.role.service.SysEveRoleMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveRoleMenuServiceImpl
 * @Description: 角色与桌面/菜单/权限点(PC端)关系业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 9:54
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "角色与桌面/菜单/权限点(PC端)", groupName = "角色管理")
public class SysEveRoleMenuServiceImpl extends SkyeyeBusinessServiceImpl<SysEveRoleMenuDao, SysEveRoleMenu> implements SysEveRoleMenuService {

    @Override
    public void deleteByRoleId(String roleId) {
        QueryWrapper<SysEveRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveRoleMenu::getRoleId), roleId);
        remove(queryWrapper);
    }

    @Override
    public void createRoleMenu(String roleId, List<String> menuIds, String createId, String createTime) {
        deleteByRoleId(roleId);
        if (CollectionUtil.isEmpty(menuIds)) {
            return;
        }
        List<SysEveRoleMenu> roleMenuList = new ArrayList<>();
        for (String menuId : menuIds) {
            SysEveRoleMenu roleMenu = new SysEveRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreater(createId);
            roleMenu.setCreateTime(createTime);
            roleMenuList.add(roleMenu);
        }
        createEntity(roleMenuList, createId);
    }

    @Override
    public List<String> querySysRoleMenuIdByRoleId(String roleId) {
        QueryWrapper<SysEveRoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveRoleMenu::getRoleId), roleId);
        List<SysEveRoleMenu> roleMenuList = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(roleMenuList)) {
            return roleMenuList.stream().map(SysEveRoleMenu::getMenuId).distinct().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public Map<String, List<String>> querySysRoleMenuIdByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isNotEmpty(roleIds)) {
            QueryWrapper<SysEveRoleMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(MybatisPlusUtil.toColumns(SysEveRoleMenu::getRoleId), roleIds);
            List<SysEveRoleMenu> roleMenuList = list(queryWrapper);
            if (CollectionUtil.isNotEmpty(roleMenuList)) {
                return roleMenuList.stream().collect(Collectors.groupingBy(SysEveRoleMenu::getRoleId,
                    Collectors.mapping(SysEveRoleMenu::getMenuId, Collectors.toList())));
            }
        }
        return new HashMap<>();
    }

}
