/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.win.entity.SysEveUserCustomParent;

import java.util.List;

/**
 * @ClassName: SysEveUserCustomParentService
 * @Description: 用户菜单自定义拖拽组合服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/5 20:44
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserCustomParentService extends SkyeyeBusinessService<SysEveUserCustomParent> {

    void deleteByParentId(String parentId);

    void deleteByMenuId(String menuId, String userId);

    List<SysEveUserCustomParent> querySysEveUserCustomParentByUserId(String userId);

}
