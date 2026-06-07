/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalFaq;

/**
 * 官网常见问题服务接口
 */
public interface PortalFaqService extends SkyeyeBusinessService<PortalFaq> {

    /**
     * 官网展示：获取已启用常见问题列表（按排序倒序）
     */
    void queryEnabledPortalFaqList(InputObject inputObject, OutputObject outputObject);
}
