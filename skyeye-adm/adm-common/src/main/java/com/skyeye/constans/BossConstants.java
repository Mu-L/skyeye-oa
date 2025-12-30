/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.constans;

import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.jedis.JedisClientService;

/**
 * @ClassName: BossConstants
 * @Description: 招聘模块常用类
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/16 19:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class BossConstants {

    public static void deleteCache(String userId) {
        JedisClientService jedisClient = SpringUtils.getBean(JedisClientService.class);
        IAuthUserService iAuthUserService = SpringUtils.getBean(IAuthUserService.class);
        String cacheKey = iAuthUserService.queryCacheKeyById(userId);
        // 删除缓存中的用户信息
        jedisClient.del(cacheKey);
    }

}
