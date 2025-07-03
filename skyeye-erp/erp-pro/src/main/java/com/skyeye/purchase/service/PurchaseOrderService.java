/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.purchase.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.purchase.entity.PurchaseOrder;

import java.util.Map;

/**
 * @ClassName: PurchaseOrderService
 * @Description: 采购订单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PurchaseOrderService extends SkyeyeErpOrderService<PurchaseOrder> {

    /**
     * 采购单信息转采购入库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void insertPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject);

    /**
     * 采购订单信息转到货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    void insertPurchaseOrderToTurnDelivery(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改质检状态
     *
     * @param id                采购订单id
     * @param qualityInspection 质检状态
     */
    void editQualityInspection(String id, Integer qualityInspection);

    Map<String, Integer> calcMaterialNormsNumByFromId(String fromId);

    void queryPurchaseOrderTransById(InputObject inputObject, OutputObject outputObject);

    void insertPurchaseOrderToReturns(InputObject inputObject, OutputObject outputObject);

    void insertPurchaseOrderToExchanges(InputObject inputObject, OutputObject outputObject);

    void queryNoPagePurchaseorderList(InputObject inputObject, OutputObject outputObject);
}
