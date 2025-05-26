package com.skyeye.rest.crm.contract.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.crm.contract.rest.ICrmContractRest;
import com.skyeye.rest.crm.contract.service.ICrmContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ICrmContractServiceImpl
 * @Description: 客户合同信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class ICrmContractServiceImpl extends IServiceImpl implements ICrmContractService {

    @Autowired
    private ICrmContractRest iCrmContractRest;

    @Override
    public List<Map<String, Object>> queryCrmContractByIds(String ids) {
        return ExecuteFeignClient.get(() -> iCrmContractRest.queryCrmContractByIds(ids)).getRows();
    }
}
