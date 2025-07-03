/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.whole.service;

import com.skyeye.business.service.SkyeyeErpOrderService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.whole.entity.WholeOrderOut;

/**
 * @ClassName: WholeOrderOutService
 * @Description: 整单委外单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/22 20:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WholeOrderOutService extends SkyeyeErpOrderService<WholeOrderOut> {

    /**
     * 修改到货状态
     *
     * @param id           采购订单id
     * @param arrivalState 到货状态
     */
    void editArrivalState(String id, Integer arrivalState);

    /**
     * 修改质检状态
     *
     * @param id                采购订单id
     * @param qualityInspection 质检状态
     */
    void editQualityInspection(String id, Integer qualityInspection);

    void queryWholeOrderOutTransById(InputObject inputObject, OutputObject outputObject);

    void insertWholeOrderOutToTurnPut(InputObject inputObject, OutputObject outputObject);

    void insertWholeOrderOutToTurnDelivery(InputObject inputObject, OutputObject outputObject);

    void insertWholeOrderOutToReturns(InputObject inputObject, OutputObject outputObject);

    void insertWholeOrderOutToExchanges(InputObject inputObject, OutputObject outputObject);

    void queryNoPageWholeOrderOutList(InputObject inputObject, OutputObject outputObject);
}
