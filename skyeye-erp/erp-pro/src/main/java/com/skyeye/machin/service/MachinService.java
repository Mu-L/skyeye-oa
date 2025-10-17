/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machin.entity.Machin;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MachinService
 * @Description: 加工单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/29 17:24
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MachinService extends SkyeyeBusinessService<Machin> {

    void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    void editPickStateById(String id, String pickState);

    Map<String, Integer> calcMaterialNormsNumByFromId(String fromId);

    void setMachinMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    void queryMachinForGanttById(InputObject inputObject, OutputObject outputObject);

    void queryMachinTransRequestById(InputObject inputObject, OutputObject outputObject);

    void insertMachinToPickRequest(InputObject inputObject, OutputObject outputObject);

    void insertMachinToPickPatch(InputObject inputObject, OutputObject outputObject);

    void queryMachinTransReturnById(InputObject inputObject, OutputObject outputObject);

    void insertMachinToPickReturn(InputObject inputObject, OutputObject outputObject);

    /**
     * 判断指定工序是否是指定加工单子单据的最后一条工序
     *
     * @param machin         加工单
     * @param childId        加工单子单据id
     * @param bomChildId     bom子件清单的id
     * @param wayProcedureId 工艺路线id
     * @param materialId     物料id
     * @param normsId        规格id
     * @param procedureId    工序id
     * @return true：是最后一条子单据，false：不是最后一条子单据
     */
    boolean checkIsLastProcedure(Machin machin, String childId, String bomChildId, String wayProcedureId, String materialId,
                                 String normsId, String procedureId);
}
