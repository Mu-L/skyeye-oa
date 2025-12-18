/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.project.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.rest.project.rest.IProProjectRest;
import com.skyeye.rest.project.service.IProProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: IProProjectServiceImpl
 * @Description: 项目信息管理公共的一些操作
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:32
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class IProProjectServiceImpl extends IServiceImpl implements IProProjectService {

    @Autowired
    private IProProjectRest iProProjectRest;

    @Override
    public Map<String, Object> queryEntityMationById(String id) {
        return queryEntityMationByIds(id).stream().findFirst().orElse(new HashMap<>());
    }

    @Override
    public List<Map<String, Object>> queryEntityMationByIds(String ids) {
        return ExecuteFeignClient.get(() -> iProProjectRest.queryProProjectByIds(ids)).getRows();
    }

    @Override
    public String queryCacheKeyById(String id) {
        return String.format(Locale.ROOT, "%s:%s", CacheConstants.PM_PROJECT_CACHE_KEY, id);
    }

}
