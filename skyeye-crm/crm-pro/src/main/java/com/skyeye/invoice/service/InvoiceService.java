/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.invoice.entity.Invoice;

/**
 * @ClassName: InvoiceService
 * @Description: 发票服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 19:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface InvoiceService extends SkyeyeBusinessService<Invoice> {

    void queryAllInvoiceList(InputObject inputObject, OutputObject outputObject);

    void queryInvoiceStatistics(InputObject inputObject, OutputObject outputObject);

    void queryAllInvoicesLists(InputObject inputObject, OutputObject outputObject);
}
