/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.wall.user.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.rest.wall.user.rest.IUserRest;
import com.skyeye.rest.wall.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: IUserServiceImpl
 * @Description: 学生信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IUserServiceImpl extends IServiceImpl implements IUserService {

    @Autowired
    private IUserRest iUserRest;

    @Override
    public Map<String, Object> queryEntityMationById(String id) {
        return queryEntityMationByIds(id).stream().findFirst().orElse(new HashMap<>());
    }

    @Override
    public List<Map<String, Object>> queryEntityMationByIds(String ids) {
        return ExecuteFeignClient.get(() -> iUserRest.queryUserByIds(ids)).getRows();
    }

    @Override
    public String queryCacheKeyById(String userId) {
        return String.format(Locale.ROOT, "%s:%s", CacheConstants.WALL_USER_CACHE_KEY, userId);
    }

    @Override
    public void deleteUsersByIds(List<String> userIds) {
    }

    @Override
    public List<Map<String, Object>> queryUserByRealNameOrStudentNumber(CommonPageInfo commonPageInfo) {
        return ExecuteFeignClient.get(() -> iUserRest.queryUserByRealNameOrStudentNumber(commonPageInfo)).getRows();
    }
}
