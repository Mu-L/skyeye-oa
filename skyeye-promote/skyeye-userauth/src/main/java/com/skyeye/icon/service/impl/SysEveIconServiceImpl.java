/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.icon.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.icon.dao.SysEveIconDao;
import com.skyeye.icon.entity.SysEveIcon;
import com.skyeye.icon.service.SysEveIconService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SysEveIconServiceImpl
 * @Description: 系统icon库服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 21:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "系统icon库", groupName = "系统icon库", tenant = TenantEnum.PLATE)
public class SysEveIconServiceImpl extends SkyeyeBusinessServiceImpl<SysEveIconDao, SysEveIcon> implements SysEveIconService {

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }
}
