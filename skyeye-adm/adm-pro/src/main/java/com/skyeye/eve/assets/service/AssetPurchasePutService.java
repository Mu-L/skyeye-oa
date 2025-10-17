/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.assets.entity.AssetPurchasePut;

import java.util.Map;

/**
 * @ClassName: AssetPurchasePutService
 * @Description: 资产采购入库服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/19 19:25
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AssetPurchasePutService extends SkyeyeBusinessService<AssetPurchasePut> {

    Map<String, Integer> calcAssetNumByFromId(String fromId);

}
