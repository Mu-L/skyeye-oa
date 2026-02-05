/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dispatch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dispatch.classenum.DispatchRuleCodeEnum;
import com.skyeye.dispatch.dao.SealDispatchConfigDao;
import com.skyeye.dispatch.entity.SealDispatchConfig;
import com.skyeye.dispatch.service.SealDispatchConfigService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SealDispatchConfigServiceImpl
 * @Description: 工单派单规则配置服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "派单规则配置", groupName = "工单管理")
public class SealDispatchConfigServiceImpl extends SkyeyeBusinessServiceImpl<SealDispatchConfigDao, SealDispatchConfig> implements SealDispatchConfigService {

    @Override
    public void getDispatchRules(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<SealDispatchConfig> queryWrapper = new QueryWrapper<>();
        SealDispatchConfig config = getOne(queryWrapper, false);
        outputObject.setBean(config);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public SealDispatchConfig getConfigForTenant() {
        QueryWrapper<SealDispatchConfig> queryWrapper = new QueryWrapper<>();
        SealDispatchConfig config = getOne(queryWrapper, false);
        if (config == null) {
            config = new SealDispatchConfig();
            config.setCapOrderQuantity(10);
            config.setEvenAssignmentEnabled(WhetherEnum.ENABLE_USING.getKey());
            config.setPoolCapQuantity(0);
            config.setPoolCountSuspended(WhetherEnum.ENABLE_USING.getKey());
            config.setSystemRules(DispatchRuleCodeEnum.toRuleList());
        }
        return config;
    }

}
