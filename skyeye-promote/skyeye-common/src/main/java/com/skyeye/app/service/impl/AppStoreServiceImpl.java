/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.app.dao.AppStoreDao;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.enums.Platform;
import com.skyeye.app.service.AppStoreService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: AppStoreServiceImpl
 * @Description: 应用商店服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 14:21
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "应用商店", groupName = "APP版本发布模块", tenant = TenantEnum.PLATE)
public class AppStoreServiceImpl extends SkyeyeBusinessServiceImpl<AppStoreDao, AppStore> implements AppStoreService {

    @Override
    protected void validatorEntity(AppStore entity) {
        super.validatorEntity(entity);
        QueryWrapper<AppStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppStore::getPlatform), entity.getPlatform());
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(AppStore::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(AppStore::getStoreKey), entity.getStoreKey()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AppStore appStore = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(appStore)) {
            throw new CustomException(Platform.getName(entity.getPlatform()) + "平台下应用商店名称/应用商店标识已存在，请勿重复添加！");
        }
    }

    @Override
    public void queryAllEnableAppStoreList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<AppStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AppStore::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<AppStore> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
