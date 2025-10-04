/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.lifecycle.dao.LifecycleTemplateEdgesDao;
import com.skyeye.lifecycle.entity.LifecycleTemplateEdges;
import com.skyeye.lifecycle.service.LifecycleTemplateEdgesService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: LifecycleTemplateEdgesServiceImpl
 * @Description: 生命周期模板连线业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 14:49
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板连线管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateEdgesServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateEdgesDao, LifecycleTemplateEdges> implements LifecycleTemplateEdgesService {

}
