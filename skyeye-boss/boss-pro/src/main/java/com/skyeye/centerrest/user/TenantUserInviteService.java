/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.centerrest.user;

import com.skyeye.centerrest.entity.tenant.TenantUserInviteRest;
import com.skyeye.common.client.ClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: TenantUserInviteService
 * @Description: 租户用户邀请服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/26 14:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@FeignClient(value = "${webroot.skyeye-pro}", configuration = ClientConfiguration.class)
public interface TenantUserInviteService {

    /**
     * 邀请员工加入
     *
     * @param tenantUserInviteRest 邀请信息
     * @return
     */
    @PostMapping("/inviteUsersToJoin")
    String inviteUsersToJoin(TenantUserInviteRest tenantUserInviteRest);

}
