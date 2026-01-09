/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ordertype.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.ordertype.entity.SealOrderTypeAllowStaff;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealOrderTypeAllowStaffService
 * @Description: 工单类型允许的接单人服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SealOrderTypeAllowStaffService extends SkyeyeBusinessService<SealOrderTypeAllowStaff> {

    void saveList(String orderTypeId, List<String> staffIds);

    void deleteByOrderTypeId(String orderTypeId);

    List<SealOrderTypeAllowStaff> selectByOrderTypeId(String orderTypeId);

    Map<String, List<SealOrderTypeAllowStaff>> selectByOrderTypeIds(List<String> orderTypeIds);

}
