/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.portal.dao.PortalDownloadVersionDao;
import com.skyeye.portal.entity.PortalDownloadVersion;
import com.skyeye.portal.service.PortalDownloadVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * 官网下载中心版本服务实现
 */
@Service
@SkyeyeService(name = "官网下载中心", groupName = "门户管理", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class PortalDownloadVersionServiceImpl extends SkyeyeBusinessServiceImpl<PortalDownloadVersionDao, PortalDownloadVersion> implements PortalDownloadVersionService {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected QueryWrapper<PortalDownloadVersion> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PortalDownloadVersion> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(PortalDownloadVersion::getOrderBy))
            .orderByDesc(MybatisPlusUtil.toColumns(PortalDownloadVersion::getReleaseDate));
        return queryWrapper;
    }

    @Override
    protected void writePostpose(PortalDownloadVersion entity, String userId) {
        super.writePostpose(entity, userId);
        jedisClientService.del(getCacheKey());
    }

    @Override
    protected void deletePostpose(PortalDownloadVersion entity) {
        jedisClientService.del(getCacheKey());
    }

    @Override
    @IgnoreTenant
    public void queryEnabledPortalDownloadVersionList(InputObject inputObject, OutputObject outputObject) {
        String cacheKey = getCacheKey();
        List<PortalDownloadVersion> result = redisCache.getList(cacheKey, key -> {
            QueryWrapper<PortalDownloadVersion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(PortalDownloadVersion::getEnabled), EnableEnum.ENABLE_USING.getKey())
                .orderByDesc(MybatisPlusUtil.toColumns(PortalDownloadVersion::getOrderBy))
                .orderByDesc(MybatisPlusUtil.toColumns(PortalDownloadVersion::getReleaseDate));
            return list(queryWrapper);
        }, RedisConstants.A_YEAR_SECONDS, PortalDownloadVersion.class);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    private String getCacheKey() {
        return String.format(Locale.ROOT, "portal:download:version");
    }

}
