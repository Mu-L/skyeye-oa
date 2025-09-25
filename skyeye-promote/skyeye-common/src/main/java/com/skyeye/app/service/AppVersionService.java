/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.service;

import com.skyeye.app.entity.AppVersion;
import com.skyeye.base.business.service.SkyeyeBusinessService;

/**
 * @ClassName: AppVersionService
 * @Description: APP版本管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AppVersionService extends SkyeyeBusinessService<AppVersion> {

    AppVersion getLatestVersionByProjectAndPlatform(String projectId, String platform, String storeKey);

}
