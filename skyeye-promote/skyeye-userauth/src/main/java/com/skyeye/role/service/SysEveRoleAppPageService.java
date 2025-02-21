/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.role.entity.SysEveRoleAppPage;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveRoleAppPageService
 * @Description: 角色与桌面/菜单(移动端)关联业务层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 10:54
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveRoleAppPageService extends SkyeyeBusinessService<SysEveRoleAppPage> {

    void deleteByRoleId(String roleId);

    void createRoleAppPage(String roleId, List<String> appPageIds);

    List<String> querySysRoleAppPageIdByRoleId(String roleId);

    Map<String, List<String>> querySysRoleAppPageIdByRoleIds(List<String> roleIds);

}
