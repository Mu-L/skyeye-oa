/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.win.entity.SysEveUserCustomMenu;

/**
 * @ClassName: SysEveUserCustomMenuService
 * @Description: 用户自定义菜单服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/5 21:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveUserCustomMenuService extends SkyeyeBusinessService<SysEveUserCustomMenu> {

    void deleteByParentId(String parentId);

}
