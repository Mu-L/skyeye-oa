/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.tenant.entity.TenantAppMenu;

import java.util.List;

/**
 * @ClassName: TenantAppMenuService
 * @Description: 应用与菜单的关系管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:53
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TenantAppMenuService extends SkyeyeBusinessService<TenantAppMenu> {

    void saveList(String appId, Integer type, List<TenantAppMenu> beans);

    void deleteByAppId(String appId, Integer type);

    void deleteByAppId(String appId);

    List<TenantAppMenu> selectByAppId(String appId, Integer type);

    List<String> selectObjectIdsByAppId(String appId, Integer type);

    List<String> selectObjectIdsByAppId(List<String> appId, Integer type);


}
