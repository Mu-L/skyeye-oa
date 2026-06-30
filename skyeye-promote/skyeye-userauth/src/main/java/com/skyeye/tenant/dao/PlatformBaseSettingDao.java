/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.dao;

import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.tenant.entity.PlatformBaseSetting;

/**
 * @ClassName: PlatformBaseSettingDao
 * @Description: 平台基础信息设置数据接口层（单表单记录，无租户隔离）
 */
public interface PlatformBaseSettingDao extends SkyeyeBaseMapper<PlatformBaseSetting> {

}
