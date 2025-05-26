package com.skyeye.rest.ifs.receivepayment.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.ifs.receivepayment.rest.IFsReceivePaymentRest;
import com.skyeye.rest.ifs.receivepayment.service.IfsReceivePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: IfsReceivePaymentServiceImpl
 * @Description: 财务收付款服务
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/15 10:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
public class IfsReceivePaymentServiceImpl extends IServiceImpl implements IfsReceivePaymentService {

    @Autowired
    private IFsReceivePaymentRest iFsReceivePaymentRest;

    @Override
    public Map<String, Object> addIFsReceivePayment(Map<String, Object> map) {
        return ExecuteFeignClient.get(() -> iFsReceivePaymentRest.addIFsReceivePayment(map)).getBean();
    }
}
