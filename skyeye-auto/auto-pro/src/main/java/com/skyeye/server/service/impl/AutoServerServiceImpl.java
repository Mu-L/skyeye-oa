/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.server.classnum.AutoServerAuthEnum;
import com.skyeye.server.dao.AutoServerDao;
import com.skyeye.server.entity.AutoServer;
import com.skyeye.server.service.AutoServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoServerServiceImpl
 * @Description: 服务器管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "服务器管理", groupName = "服务器管理", teamAuth = true)
public class AutoServerServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoServerDao, AutoServer> implements AutoServerService {

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Override
    public Class getAuthEnumClass() {
        return AutoServerAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoServerAuthEnum.ADD.getKey(), AutoServerAuthEnum.EDIT.getKey(), AutoServerAuthEnum.DELETE.getKey());
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoServerList(commonPageInfo);
        autoEnvironmentService.setMationForMap(beans, "environmentId", "environmentMation");
        return beans;
    }

    @Override
    public AutoServer selectById(String id) {
        AutoServer autoServer = super.selectById(id);
        autoEnvironmentService.setDataMation(autoServer, AutoServer::getEnvironmentId);
        return autoServer;
    }

    @Override
    public List<AutoServer> selectByIds(String... ids) {
        List<AutoServer> autoServers = super.selectByIds(ids);
        autoEnvironmentService.setDataMation(autoServers, AutoServer::getEnvironmentId);
        return autoServers;
    }

    @Override
    public void queryAutoServerListByEnvironmentId(InputObject inputObject, OutputObject outputObject) {
        String environmentId = inputObject.getParams().get("environmentId").toString();
        if (StrUtil.isEmpty(environmentId)) {
            return;
        }
        QueryWrapper<AutoServer> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoServer::getEnvironmentId), environmentId);
        List<AutoServer> result = list(queryWrapper);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }
}
