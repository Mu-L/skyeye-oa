/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.app.dao.AppVersionDao;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.service.AppVersionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @ClassName: AppVersionServiceImpl
 * @Description: APP版本管理服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "APP版本管理", groupName = "APP版本发布模块", tenant = TenantEnum.PLATE)
public class AppVersionServiceImpl extends SkyeyeBusinessServiceImpl<AppVersionDao, AppVersion> implements AppVersionService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    protected void validatorEntity(AppVersion entity) {
        super.validatorEntity(entity);
        QueryWrapper<AppVersion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getProjectId), entity.getProjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getPlatform), entity.getPlatform());
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(AppVersion::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(AppVersion::getVersionCode), entity.getVersionCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AppVersion appVersion = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(appVersion)) {
            throw new CustomException("该版本名称或版本号已存在，请重新输入！");
        }
        String appFilePath = tPath.replace("images", StrUtil.EMPTY) + entity.getFilePath();
        File appFile = new File(appFilePath);
        entity.setFileSize(String.valueOf(appFile.length()));
    }

    @Override
    protected void writePostpose(AppVersion entity, String userId) {
        super.writePostpose(entity, userId);

    }
}
