/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.business.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.entity.ErpOrderItem;
import com.skyeye.entity.TransmitObject;

import java.util.List;

/**
 * @ClassName: SkyeyeErpOrderItemService
 * @Description: ERP单据关联的商品的service接口
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 20:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SkyeyeErpOrderItemService extends SkyeyeLinkDataService<ErpOrderItem> {

    /**
     * 计算单据信息的总价
     *
     * @param object
     * @param erpOrderItemList
     * @return
     */
    List<ErpOrderItem> calcOrderAllTotalPrice(TransmitObject object, List<ErpOrderItem> erpOrderItemList);

    List<ErpOrderItem> queryErpOrderItemByPIds(List<String> pIds);

    List<ErpOrderItem> queryHolderOutPutNormsList(String holderKey, String type, String holderId);
}
