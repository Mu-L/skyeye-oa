/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service;

import com.skyeye.afterseal.entity.AfterSeal;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * @ClassName: AfterSealService
 * @Description: 工单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/10 13:21
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface AfterSealService extends SkyeyeBusinessService<AfterSeal> {

    void editSealSeServiceWaitToWorkMation(InputObject inputObject, OutputObject outputObject);

    void receivingSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject);

    void updateStateById(String id, String state);

    void querySealSeServiceSignon(InputObject inputObject, OutputObject outputObject);

    void queryMyParticipatedPendingCompletedOrders(InputObject inputObject, OutputObject outputObject);

    void auditSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject);

    void finishSealSeServiceOrderById(InputObject inputObject, OutputObject outputObject);

    void queryOverviewSealSeServiceOrder(InputObject inputObject, OutputObject outputObject);

    void querySealSeServiceOrderTypeStats(InputObject inputObject, OutputObject outputObject);

    void querySealSeServiceOrderTrendStats(InputObject inputObject, OutputObject outputObject);

    void querySealServiceOrderWorkerStats(InputObject inputObject, OutputObject outputObject);

}
