/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.role.entity.SysEveRoleAppPageAuth;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveRoleAppPageAuthService
 * @Description: 角色与权限点(移动端)关联业务层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 11:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveRoleAppPageAuthService extends SkyeyeBusinessService<SysEveRoleAppPageAuth> {

    void deleteByRoleId(String roleId);

    void createRoleAppPageAuth(String roleId, List<String> authIds);

    List<String> queryRoleAppPageAuthByRoleId(String roleId);

    Map<String, List<String>> queryRoleAppPageAuthByRoleIds(List<String> roleIds);

}
