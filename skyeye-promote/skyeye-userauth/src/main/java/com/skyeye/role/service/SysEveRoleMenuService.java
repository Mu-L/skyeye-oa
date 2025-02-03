/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.role.entity.SysEveRoleMenu;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveRoleMenuService
 * @Description: 角色与桌面/菜单/权限点(PC端)关系业务层
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/3 9:54
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveRoleMenuService extends SkyeyeBusinessService<SysEveRoleMenu> {

    void deleteByRoleId(String roleId);

    void createRoleMenu(String roleId, List<String> menuIds, String createId, String createTime);

    List<String> querySysRoleMenuIdByRoleId(String roleId);

    Map<String, List<String>> querySysRoleMenuIdByRoleIds(List<String> roleIds);

}
