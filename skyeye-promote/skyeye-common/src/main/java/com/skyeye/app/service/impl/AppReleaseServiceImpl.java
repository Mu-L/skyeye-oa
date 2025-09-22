/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.app.dao.AppReleaseDao;
import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.service.AppReleaseService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: AppReleaseServiceImpl
 * @Description: APP版本发布信息业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 14:57
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "APP版本发布信息", groupName = "APP版本发布模块", tenant = TenantEnum.PLATE)
public class AppReleaseServiceImpl extends SkyeyeBusinessServiceImpl<AppReleaseDao, AppRelease> implements AppReleaseService {

    @Override
    public void saveList(String versionId, String projectId, List<AppRelease> beans) {
        if (CollectionUtil.isNotEmpty(beans)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            for (AppRelease appRelease : beans) {
                appRelease.setVersionId(versionId);
                appRelease.setProjectId(projectId);
            }
            createEntity(beans, userId);
        }
    }

    @Override
    public List<AppRelease> selectByVersionIdAndProjectId(String versionId, String projectId) {
        QueryWrapper<AppRelease> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getProjectId), projectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppRelease::getVersionId), versionId);
        List<AppRelease> list = list(queryWrapper);
        return list;
    }
}
