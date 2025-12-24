/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.serviceitems.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.serviceitems.entity.ServiceItems;

/**
 * @ClassName: ServiceItemsService
 * @Description: 售后服务项目服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ServiceItemsService extends SkyeyeBusinessService<ServiceItems> {

    /**
     * 查询所有启用的服务项目列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void queryEnabledServiceItemsList(InputObject inputObject, OutputObject outputObject);

    /**
     * 增加服务项目销量（+1）
     *
     * @param id 服务项目id
     */
    void incrementSalesVolume(String id);

}

