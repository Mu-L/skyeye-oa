/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.personnel.entity.SysEveUserLoginLog;

/**
 * @ClassName: SysEveUserLoginLogService
 * @Description: 用户登录日志服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 20:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserLoginLogService extends SkyeyeBusinessService<SysEveUserLoginLog> {

    void recordLoginLogAsync(String userId, String userCode, Integer deviceType, Integer loginStatus, String loginMessage);

}