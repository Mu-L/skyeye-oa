/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.production.entity.ProductionPlan;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProductionPlanService
 * @Description: 出货计划单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/21 20:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProductionPlanService extends SkyeyeBusinessService<ProductionPlan> {

    void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    Map<String, String> calcMaterialNormsNumByFromId(String fromId);

    /**
     * 修改采购状态
     *
     * @param id            出货计划单id
     * @param purchaseState 采购状态 {@link com.skyeye.production.classenum.ProductionPlanPurchaseState}
     */
    void editPurchaseState(String id, Integer purchaseState);

    /**
     * 修改生产状态
     *
     * @param id           出货计划单id
     * @param produceState 生产状态 {@link com.skyeye.production.classenum.ProductionPlanProduceState}
     */
    void editProduceState(String id, Integer produceState);

    void queryProductionPlanTransById(InputObject inputObject, OutputObject outputObject);

    void insertProductionPlanToProduction(InputObject inputObject, OutputObject outputObject);

    void queryProductionPlanTransPurchaseOrderById(InputObject inputObject, OutputObject outputObject);

    void insertProductionPlanToPurchaseOrder(InputObject inputObject, OutputObject outputObject);
}
