/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: ShopAppAuthService
 * @Description: 商城登录管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/16 11:57
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ShopAppAuthService {

    void shopLoginForPC(InputObject inputObject, OutputObject outputObject);

    void shopLoginForApp(InputObject inputObject, OutputObject outputObject);

    void shopLogout(InputObject inputObject, OutputObject outputObject);

    void editShopUserPassword(InputObject inputObject, OutputObject outputObject);

    void sendShopSmsCode(InputObject inputObject, OutputObject outputObject);

    void smsShopLogin(InputObject inputObject, OutputObject outputObject);

    void smsShopMemberRegister(InputObject inputObject, OutputObject outputObject);

    void editShopUserPasswordByPhone(InputObject inputObject, OutputObject outputObject);
}
