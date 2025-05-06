/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AppAuthService
 * @Description: 登录管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 17:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AppAuthService {

    void sendSmsCode(InputObject inputObject, OutputObject outputObject);

    void smsLogin(InputObject inputObject, OutputObject outputObject);

    void queryAuthPointByUserId(InputObject inputObject, OutputObject outputObject);

    void switchTenantSetAuthPoint(InputObject inputObject, OutputObject outputObject);
}
