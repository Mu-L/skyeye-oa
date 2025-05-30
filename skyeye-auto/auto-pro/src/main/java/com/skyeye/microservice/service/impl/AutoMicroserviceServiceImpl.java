/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.microservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.microservice.classenum.AutoMicroserviceAuthEnum;
import com.skyeye.microservice.dao.AutoMicroserviceDao;
import com.skyeye.microservice.entity.AutoMicroservice;
import com.skyeye.microservice.service.AutoMicroserviceService;
import com.skyeye.server.service.AutoServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoMicroserviceServiceImpl
 * @Description: 微服务管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 8:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "微服务管理", groupName = "微服务管理", teamAuth = true)
public class AutoMicroserviceServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoMicroserviceDao, AutoMicroservice> implements AutoMicroserviceService {

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Autowired
    private AutoServerService autoServerService;

    @Override
    public Class getAuthEnumClass() {
        return AutoMicroserviceAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoMicroserviceAuthEnum.ADD.getKey(), AutoMicroserviceAuthEnum.EDIT.getKey(), AutoMicroserviceAuthEnum.DELETE.getKey());
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoMicroserviceList(commonPageInfo);
        autoEnvironmentService.setMationForMap(beans, "environmentId", "environmentMation");
        autoServerService.setMationForMap(beans, "serverId", "serverMation");
        return beans;
    }

    @Override
    public AutoMicroservice selectById(String id) {
        AutoMicroservice microservice = super.selectById(id);
        autoEnvironmentService.setDataMation(microservice, AutoMicroservice::getEnvironmentId);
        autoServerService.setDataMation(microservice, AutoMicroservice::getServerId);
        return microservice;
    }

    @Override
    public List<AutoMicroservice> selectByIds(String... ids) {
        List<AutoMicroservice> autoMicroservices = super.selectByIds(ids);
        autoEnvironmentService.setDataMation(autoMicroservices, AutoMicroservice::getEnvironmentId);
        autoServerService.setDataMation(autoMicroservices, AutoMicroservice::getServerId);
        return autoMicroservices;
    }

    @Override
    public void queryAutoMicroserviceListByServerId(InputObject inputObject, OutputObject outputObject) {
        String serverId = inputObject.getParams().get("serverId").toString();
        QueryWrapper<AutoMicroservice> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoMicroservice::getServerId), serverId);
        List<AutoMicroservice> result = list(queryWrapper);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }
}
