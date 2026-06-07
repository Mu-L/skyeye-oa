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
import com.skyeye.portal.dao.PortalFaqDao;
import com.skyeye.portal.entity.PortalFaq;
import com.skyeye.portal.service.PortalFaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * 官网常见问题服务实现
 */
@Service
@SkyeyeService(name = "官网常见问题", groupName = "门户管理", tenant = TenantEnum.PLATE, allowDynamicAttrKey = false)
public class PortalFaqServiceImpl extends SkyeyeBusinessServiceImpl<PortalFaqDao, PortalFaq> implements PortalFaqService {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected QueryWrapper<PortalFaq> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PortalFaq> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(PortalFaq::getOrderBy))
            .orderByDesc(MybatisPlusUtil.toColumns(PortalFaq::getCreateTime));
        return queryWrapper;
    }

    @Override
    protected void writePostpose(PortalFaq entity, String userId) {
        super.writePostpose(entity, userId);
        jedisClientService.del(getCacheKey());
    }

    @Override
    protected void deletePostpose(PortalFaq entity) {
        jedisClientService.del(getCacheKey());
    }

    @Override
    @IgnoreTenant
    public void queryEnabledPortalFaqList(InputObject inputObject, OutputObject outputObject) {
        String cacheKey = getCacheKey();
        List<PortalFaq> result = redisCache.getList(cacheKey, key -> {
            QueryWrapper<PortalFaq> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(PortalFaq::getEnabled), EnableEnum.ENABLE_USING.getKey())
                .orderByDesc(MybatisPlusUtil.toColumns(PortalFaq::getOrderBy))
                .orderByDesc(MybatisPlusUtil.toColumns(PortalFaq::getCreateTime));
            return list(queryWrapper);
        }, RedisConstants.A_YEAR_SECONDS, PortalFaq.class);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    private String getCacheKey() {
        return String.format(Locale.ROOT, "portal:faq:list");
    }
}
