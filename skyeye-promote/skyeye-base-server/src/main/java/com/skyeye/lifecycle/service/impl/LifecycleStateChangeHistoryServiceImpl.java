/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.lifecycle.dao.LifecycleStateChangeHistoryDao;
import com.skyeye.lifecycle.entity.LifecycleStateChangeHistory;
import com.skyeye.lifecycle.service.LifecycleStateChangeHistoryService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: LifecycleStateChangeHistoryServiceImpl
 * @Description: 生命周期状态变更历史服务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/15 10:03
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期状态变更历史管理", groupName = "生命周期状态变更历史管理", tenant = TenantEnum.STRONG_ISOLATION)
public class LifecycleStateChangeHistoryServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleStateChangeHistoryDao, LifecycleStateChangeHistory> implements LifecycleStateChangeHistoryService {

    @Override
    public void queryNewLifecycleStateChangeHistory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        String objectAppId = params.get("objectAppId").toString();
        String templateId = params.get("templateId").toString();
        QueryWrapper<LifecycleStateChangeHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleStateChangeHistory::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleStateChangeHistory::getObjectKey), objectKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleStateChangeHistory::getObjectAppId), objectAppId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleStateChangeHistory::getTemplateId), templateId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LifecycleStateChangeHistory::getCreateTime));
        LifecycleStateChangeHistory lifecycleStateChangeHistory = getOne(queryWrapper, false);
        outputObject.setBean(lifecycleStateChangeHistory);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}
