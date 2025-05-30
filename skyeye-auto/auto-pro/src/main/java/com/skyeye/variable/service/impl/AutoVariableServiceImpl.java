/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.variable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.environment.service.AutoEnvironmentService;
import com.skyeye.variable.classenum.AutoVariableAuthEnum;
import com.skyeye.variable.dao.AutoVariableDao;
import com.skyeye.variable.entity.AutoVariable;
import com.skyeye.variable.service.AutoVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AutoVariableServiceImpl
 * @Description: 变量管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/26 9:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "变量管理", groupName = "变量管理", teamAuth = true)
public class AutoVariableServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoVariableDao, AutoVariable> implements AutoVariableService {

    @Autowired
    private AutoEnvironmentService autoEnvironmentService;

    @Override
    public Class getAuthEnumClass() {
        return AutoVariableAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoVariableAuthEnum.ADD.getKey(), AutoVariableAuthEnum.EDIT.getKey(), AutoVariableAuthEnum.DELETE.getKey());
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryAutoVariableList(commonPageInfo);
        autoEnvironmentService.setMationForMap(beans, "environmentId", "environmentMation");
        return beans;
    }

    @Override
    public Map<String, String> getAutoVariable(String type, String environmentId) {
        QueryWrapper<AutoVariable> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVariable::getType), type);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoVariable::getEnvironmentId), environmentId);
        List<AutoVariable> list = list(queryWrapper);
        Map<String, String> stringMap = list.stream().collect(Collectors.toMap(AutoVariable::getKey, AutoVariable::getValue));
        return stringMap;
    }
}
