/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalDownloadVersion;

/**
 * 官网下载中心版本服务接口
 */
public interface PortalDownloadVersionService extends SkyeyeBusinessService<PortalDownloadVersion> {

    /**
     * 官网展示：获取已启用版本列表（按排序倒序）
     */
    void queryEnabledPortalDownloadVersionList(InputObject inputObject, OutputObject outputObject);
}
