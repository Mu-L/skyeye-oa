/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantUserInvite;

/**
 * @ClassName: TenantUserInviteService
 * @Description: 租户下的用户邀请信息服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/27 8:29
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantUserInviteService extends SkyeyeBusinessService<TenantUserInvite> {

    void inviteUsersToJoin(InputObject inputObject, OutputObject outputObject);

    void cancelInviteUsersToJoin(InputObject inputObject, OutputObject outputObject);

    void resendInviteUsersToJoin(InputObject inputObject, OutputObject outputObject);

    void joinTenantByInvite(InputObject inputObject, OutputObject outputObject);

    void editInviteUsersToExit(String id, Integer exitType);
}
