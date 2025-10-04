/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.lifecycle.dao.LifecycleTemplateNodeDao;
import com.skyeye.lifecycle.entity.LifecycleTemplateNode;
import com.skyeye.lifecycle.service.LifecycleTemplateNodeService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: LifecycleTemplateNodeServiceImpl
 * @Description: 生命周期模板节点管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 14:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板节点管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateNodeServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateNodeDao, LifecycleTemplateNode> implements LifecycleTemplateNodeService {

}
