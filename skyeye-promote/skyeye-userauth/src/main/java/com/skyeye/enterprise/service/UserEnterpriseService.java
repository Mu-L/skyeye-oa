/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.enterprise.entity.UserEnterprise;

/**
 * @ClassName: UserEnterpriseService
 * @Description: 企业账户服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/15 14:15
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface UserEnterpriseService extends SkyeyeBusinessService<UserEnterprise> {

    void queryCurrentLoginUserEnterprise(InputObject inputObject, OutputObject outputObject);

    void loginUserEnterprise(InputObject inputObject, OutputObject outputObject);

    void updateUserEnterprisePassword(InputObject inputObject, OutputObject outputObject);

    void editUserEnterpriseState(String id, Integer state);

    void existUserEnterprise(InputObject inputObject, OutputObject outputObject);
}
