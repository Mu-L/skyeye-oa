package com.skyeye.receivepayment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.receivepayment.entity.ReceivePayment;

import java.util.List;

/**
 * @ClassName: ReceivePaymentService
 * @Description: 收付款管理接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ReceivePaymentService extends SkyeyeBusinessService<ReceivePayment> {
    void queryReceivePaymentByContractId(InputObject inputObject, OutputObject outputObject);

    List<ReceivePayment> getBeforeThirtyDaysReceivePayment(String tenantId);

    void updateReceivePayment(InputObject inputObject, OutputObject outputObject);
}
