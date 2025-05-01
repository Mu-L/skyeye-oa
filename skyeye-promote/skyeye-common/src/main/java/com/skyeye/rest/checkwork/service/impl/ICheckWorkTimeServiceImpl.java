/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.checkwork.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.rest.checkwork.rest.ICheckWorkTimeRest;
import com.skyeye.rest.checkwork.service.ICheckWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: ICheckWorkTimeServiceImpl
 * @Description: 考勤班次服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/1 10:14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ICheckWorkTimeServiceImpl extends IServiceImpl implements ICheckWorkTimeService {
    @Autowired
    private ICheckWorkTimeRest iCheckWorkTimeRest;

    @Override
    public Map<String, Object> queryEntityMationById(String id) {
        return queryEntityMationByIds(id).stream().findFirst().orElse(new HashMap<>());
    }

    @Override
    public List<Map<String, Object>> queryEntityMationByIds(String ids) {
        return ExecuteFeignClient.get(() -> iCheckWorkTimeRest.queryCheckWorkTimeByIds(ids)).getRows();
    }

    @Override
    public String queryCacheKeyById(String id) {
        return String.format(Locale.ROOT, "%s:%s", CacheConstants.CHECK_WORK_TIME_CACHE_KEY, id);
    }
}
