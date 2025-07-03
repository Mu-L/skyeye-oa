/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.assets.entity.AssetPurchase;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AssetPurchaseService
 * @Description: 资产采购单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetPurchaseService extends SkyeyeFlowableService<AssetPurchase> {

    void queryAssetPurchaseOrderTransById(InputObject inputObject, OutputObject outputObject);

    void insertAssetPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject);

    void insertAssetPurchaseOrderToReturns(InputObject inputObject, OutputObject outputObject);

    void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    void queryLastMonthAssetPurchaseCost(InputObject inputObject, OutputObject outputObject);
}
