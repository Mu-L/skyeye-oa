/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.depot.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOut;

import java.util.List;

/**
 * @ClassName: DepotOutService
 * @Description: 仓库出库单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/26 9:00
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DepotOutService extends SkyeyeErpOrderService<DepotOut> {

    void queryDepotOutTransById(InputObject inputObject, OutputObject outputObject);

    void insertDepotOutToTurnPut(InputObject inputObject, OutputObject outputObject);

    void insertDepotOutToSealsReturns(InputObject inputObject, OutputObject outputObject);

    void queryNeedConfirmDepotOutList(InputObject inputObject, OutputObject outputObject);

    void queryNeedStoreConfirmDepotOutList(InputObject inputObject, OutputObject outputObject);

    void queryDepotOutTransStoreById(InputObject inputObject, OutputObject outputObject);

    void insertDepotOutToTurnStorePut(InputObject inputObject, OutputObject outputObject);

    void insertDepotOutToStoreSealsReturns(InputObject inputObject, OutputObject outputObject);

    List<DepotOut> queryLeadByHolderId(String holderId);

}
