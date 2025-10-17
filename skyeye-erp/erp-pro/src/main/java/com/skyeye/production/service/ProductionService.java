/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.production.entity.Production;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProductionService
 * @Description: 生产计划单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/29 11:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProductionService extends SkyeyeBusinessService<Production> {

    void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    void editOutState(String id, Integer outState);

    void editMachinOrderState(String id, Integer machinOrderState);

    Map<String, Integer> calcMaterialNormsNumByFromId(String fromId);

    void queryProductionTransById(InputObject inputObject, OutputObject outputObject);

    void insertProductionToMachin(InputObject inputObject, OutputObject outputObject);

    void queryProductionTransWholeById(InputObject inputObject, OutputObject outputObject);

    void insertProductionToWhole(InputObject inputObject, OutputObject outputObject);
}
