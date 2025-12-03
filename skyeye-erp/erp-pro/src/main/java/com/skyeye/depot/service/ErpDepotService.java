/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.Depot;

import java.util.List;

/**
 * @ClassName: ErpDepotService
 * @Description: 仓库管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2019/9/14 10:44
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ErpDepotService extends SkyeyeBusinessService<Depot> {

    void queryAllStoreHouseList(InputObject inputObject, OutputObject outputObject);

    void queryStoreHouseListByCurrentUserId(InputObject inputObject, OutputObject outputObject);

    List<Depot> queryDepotListByChargePerson(String userId, String enabled);
}
