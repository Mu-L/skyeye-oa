package com.skyeye.rest.crm.payment.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.crm.payment.rest.ICrmPaymentCollectionRest;
import com.skyeye.rest.crm.payment.service.ICrmPaymentCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ICrmPaymentCollectionServiceImpl
 * @Description: 客户回款信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ICrmPaymentCollectionServiceImpl extends IServiceImpl implements ICrmPaymentCollectionService {

    @Autowired
    private ICrmPaymentCollectionRest iCrmContractRest;

    @Override
    public List<Map<String, Object>> queryPaymentCollectionById(String id) {
        return ExecuteFeignClient.get(() -> iCrmContractRest.queryPaymentCollectionById(id)).getRows();
    }
}
