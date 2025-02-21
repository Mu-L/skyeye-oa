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
import com.skyeye.role.dao.SysEveRoleAppPageAuthDao;
import com.skyeye.role.entity.SysEveRoleAppPageAuth;
import com.skyeye.role.service.SysEveRoleAppPageAuthService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SysEveRoleAppPageAuthServiceImpl
 * @Description: 角色与权限点(移动端)业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 11:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "角色与权限点(移动端)", groupName = "角色管理")
public class SysEveRoleAppPageAuthServiceImpl extends SkyeyeBusinessServiceImpl<SysEveRoleAppPageAuthDao, SysEveRoleAppPageAuth> implements SysEveRoleAppPageAuthService {

    @Override
    public void deleteByRoleId(String roleId) {
        QueryWrapper<SysEveRoleAppPageAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveRoleAppPageAuth::getRoleId), roleId);
        remove(queryWrapper);
    }

    @Override
    public void createRoleAppPageAuth(String roleId, List<String> authIds) {
        deleteByRoleId(roleId);
        if (CollectionUtil.isEmpty(authIds)) {
            return;
        }
        List<SysEveRoleAppPageAuth> sysEveRoleAppPageAuthList = new ArrayList<>();
        for (String authId : authIds) {
            SysEveRoleAppPageAuth sysEveRoleAppPageAuth = new SysEveRoleAppPageAuth();
            sysEveRoleAppPageAuth.setRoleId(roleId);
            sysEveRoleAppPageAuth.setAuthId(authId);
            sysEveRoleAppPageAuthList.add(sysEveRoleAppPageAuth);
        }
        createEntity(sysEveRoleAppPageAuthList, StrUtil.EMPTY);
    }

    @Override
    public List<String> queryRoleAppPageAuthByRoleId(String roleId) {
        QueryWrapper<SysEveRoleAppPageAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveRoleAppPageAuth::getRoleId), roleId);
        List<SysEveRoleAppPageAuth> sysEveRoleAppPageAuthList = list(queryWrapper);
        if (CollectionUtil.isEmpty(sysEveRoleAppPageAuthList)) {
            return new ArrayList<>();
        }
        return sysEveRoleAppPageAuthList.stream().map(SysEveRoleAppPageAuth::getAuthId).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<String, List<String>> queryRoleAppPageAuthByRoleIds(List<String> roleIds) {
        if (CollectionUtil.isNotEmpty(roleIds)) {
            QueryWrapper<SysEveRoleAppPageAuth> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(MybatisPlusUtil.toColumns(SysEveRoleAppPageAuth::getRoleId), roleIds);
            List<SysEveRoleAppPageAuth> sysEveRoleAppPageAuthList = list(queryWrapper);
            if (CollectionUtil.isNotEmpty(sysEveRoleAppPageAuthList)) {
                return sysEveRoleAppPageAuthList.stream().collect(Collectors.groupingBy(SysEveRoleAppPageAuth::getRoleId,
                    Collectors.mapping(SysEveRoleAppPageAuth::getAuthId, Collectors.toList())));
            }
        }
        return new HashMap<>();
    }
}
