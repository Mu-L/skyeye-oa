/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inspection.service;

import com.skyeye.base.business.service.SkyeyeFlowableService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.inspection.entity.QualityInspection;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: QualityInspectionService
 * @Description: 质检单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 8:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface QualityInspectionService extends SkyeyeFlowableService<QualityInspection> {

    Map<String, Integer> calcMaterialNormsNumByFromId(String... fromId);

    /**
     * 修改入库状态
     *
     * @param id       质检单id
     * @param putState 入库状态 {@link com.skyeye.inspection.classenum.QualityInspectionPutState}
     */
    void editPutState(String id, Integer putState);

    /**
     * 修改退货状态
     *
     * @param id          质检单id
     * @param returnState 退货状态 {@link com.skyeye.inspection.classenum.QualityInspectionReturnState}
     */
    void editReturnState(String id, Integer returnState);

    void editExchangesState(String id, Integer returnState);

    void setQualityInspectionMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    void queryQualityInspectionTransById(InputObject inputObject, OutputObject outputObject);

    void qualityInspectionToPurchasePut(InputObject inputObject, OutputObject outputObject);

    void queryQualityInspectionTransReturnById(InputObject inputObject, OutputObject outputObject);

    void qualityInspectionToPurchaseReturn(InputObject inputObject, OutputObject outputObject);

    void queryQualityInspectionTransExchangesById(InputObject inputObject, OutputObject outputObject);

    void qualityInspectionToPurchaseExchanges(InputObject inputObject, OutputObject outputObject);
}
