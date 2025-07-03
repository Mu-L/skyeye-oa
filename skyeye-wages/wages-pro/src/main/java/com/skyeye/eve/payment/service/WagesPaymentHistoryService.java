/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.payment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.payment.entity.WagesPaymentHistory;

import java.util.List;

/**
 * @ClassName: WagesPaymentHistoryService
 * @Description: 薪资发放历史管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/22 18:10
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface WagesPaymentHistoryService extends SkyeyeBusinessService<WagesPaymentHistory> {

    void queryAllGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject);

    void queryMyWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject);

    void queryAllNotGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject);

    List<WagesPaymentHistory> queryWagesPaymentHistoryByState(Integer state);

    void editWagesPaymentHistoryState(String staffId, String payMonth, Integer state);

    void queryWagesStaffPaymentDetail(InputObject inputObject, OutputObject outputObject);
}
