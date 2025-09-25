/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.app.dao.AppProjectDao;
import com.skyeye.app.entity.AppProject;
import com.skyeye.app.service.AppProjectService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @ClassName: AppProjectServiceImpl
 * @Description: APP项目管理服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "App项目管理", groupName = "APP版本发布模块", tenant = TenantEnum.PLATE)
public class AppProjectServiceImpl extends SkyeyeBusinessServiceImpl<AppProjectDao, AppProject> implements AppProjectService {

    @Override
    protected void validatorEntity(AppProject entity) {
        super.validatorEntity(entity);
        QueryWrapper<AppProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppProject::getProjectKey), entity.getProjectKey());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AppProject appProject = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(appProject)) {
            throw new CustomException("项目标识已存在，请重新输入！");
        }
    }

    @Override
    public AppProject selectByKey(String projectKey) {
        QueryWrapper<AppProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppProject::getProjectKey), projectKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppProject::getEnabled), EnableEnum.ENABLE_USING.getKey());
        return getOne(queryWrapper, false);
    }
}
