package com.skyeye.rest.erp.payment.service;

import com.skyeye.base.rest.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IErpPaymentCollectionService
 * @Description: 供应商付款信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IErpPaymentCollectionService extends IService {
    List<Map<String, Object>> queryPaymentCollectionById(String id);

}
