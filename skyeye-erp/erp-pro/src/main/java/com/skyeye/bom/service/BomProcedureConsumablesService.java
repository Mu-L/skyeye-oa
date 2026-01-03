/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.bom.entity.BomProcedureConsumables;

import java.util.List;

/**
 * @ClassName: BomProcedureConsumablesService
 * @Description: BOM工序耗材服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface BomProcedureConsumablesService extends SkyeyeBusinessService<BomProcedureConsumables> {

    /**
     * 根据BOM子件ID查询工序耗材列表
     *
     * @param bomChildId BOM子件ID
     * @return 工序耗材列表
     */
    List<BomProcedureConsumables> queryListByBomChildId(String bomChildId);

    /**
     * 根据BOM子件ID列表批量查询工序耗材
     *
     * @param bomChildIds BOM子件ID列表
     * @return 工序耗材Map，key为bomChildId
     */
    java.util.Map<String, List<BomProcedureConsumables>> queryListByBomChildIds(List<String> bomChildIds);

    /**
     * 根据BOM子件ID删除工序耗材
     *
     * @param bomId           BOM的ID
     */
    void deleteByBomId(String bomId);

    /**
     * 批量保存工序耗材
     *
     * @param bomId           BOM的ID
     * @param consumablesList 工序耗材列表
     */
    void saveList(String bomId, List<BomProcedureConsumables> consumablesList);
}

