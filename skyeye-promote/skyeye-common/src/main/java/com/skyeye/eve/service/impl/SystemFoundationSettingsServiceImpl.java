/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.dao.SystemFoundationSettingsDao;
import com.skyeye.eve.entity.SystemFoundationSettings;
import com.skyeye.eve.service.SystemFoundationSettingsService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SystemFoundationSettingsServiceImpl
 * @Description: 系统基础设置服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/6 22:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "系统基础设置", groupName = "系统基础设置")
public class SystemFoundationSettingsServiceImpl extends SkyeyeBusinessServiceImpl<SystemFoundationSettingsDao, SystemFoundationSettings> implements SystemFoundationSettingsService {

    @Override
    public void querySystemFoundationSettingsList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<SystemFoundationSettings> queryWrapper = new QueryWrapper<>();
        SystemFoundationSettings systemFoundationSettings = getOne(queryWrapper, false);
        outputObject.setBean(systemFoundationSettings);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}
