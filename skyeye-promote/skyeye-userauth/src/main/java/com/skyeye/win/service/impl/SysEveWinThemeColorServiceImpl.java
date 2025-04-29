/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.win.dao.SysEveWinThemeColorDao;
import com.skyeye.win.entity.SysEveWinThemeColor;
import com.skyeye.win.service.SysEveWinThemeColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: SysEveWinThemeColorServiceImpl
 * @Description: win系统主题颜色服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/22 12:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "win系统主题颜色", groupName = "win系统主题颜色", tenant = TenantEnum.PLATE)
public class SysEveWinThemeColorServiceImpl extends SkyeyeBusinessServiceImpl<SysEveWinThemeColorDao, SysEveWinThemeColor> implements SysEveWinThemeColorService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public void writePostpose(SysEveWinThemeColor entity, String userId) {
        jedisClientService.del(Constants.SYS_WIN_THEME_COLOR_REDIS_KEY);
    }

    @Override
    public void deletePostpose(SysEveWinThemeColor entity) {
        jedisClientService.del(Constants.SYS_WIN_THEME_COLOR_REDIS_KEY);
    }

    @Override
    @IgnoreTenant
    public List<SysEveWinThemeColor> querySysEveWinThemeColorList() {
        return redisCache.getList(Constants.SYS_WIN_THEME_COLOR_REDIS_KEY, key -> {
            return list();
        }, RedisConstants.THIRTY_DAY_SECONDS, SysEveWinThemeColor.class);
    }
}
