/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.payment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.payment.entity.Payment;

/**
 * @ClassName: PaymentService
 * @Description: 付款服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PaymentService extends SkyeyeBusinessService<Payment> {

    void queryPaymentByContractId(InputObject inputObject, OutputObject outputObject);

    /**
     * 修改已开票金额
     *
     * @param id
     * @param invoicePrice
     */
    void updateInvoicePrice(String id, String invoicePrice);

}
