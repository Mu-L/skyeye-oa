package com.skyeye.rest.erp.payable.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.payable.rest.IErpPayableRest;
import com.skyeye.rest.erp.payable.service.IErpPayableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IErpPayableServiceImpl
 * @Description: 供应商应付信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IErpPayableServiceImpl extends IServiceImpl implements IErpPayableService {

    @Autowired
    private IErpPayableRest iErpPayableRest;

    @Override
    public List<Map<String, Object>> queryPayableByIds(String ids) {
        return ExecuteFeignClient.get(() -> iErpPayableRest.queryPayableByIds(ids)).getRows();
    }

    @Override
    public void updatePayableById(String id, String price) {
        ExecuteFeignClient.get(() -> iErpPayableRest.updatePayableById(id, price));
    }
}
