package com.skyeye.rest.ifs.receivepayment.service;

import com.skyeye.base.rest.service.IService;

import java.util.Map;

/**
 * @ClassName: IfsReceivePaymentService
 * @Description: 财务收付款服务
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface IfsReceivePaymentService extends IService {

    Map<String, Object> addIFsReceivePayment(Map<String, Object> map);

    Map<String, Object> updateReceivePayment(Map<String, Object> map);
}
