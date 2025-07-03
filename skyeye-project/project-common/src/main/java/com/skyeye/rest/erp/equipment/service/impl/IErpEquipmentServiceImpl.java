package com.skyeye.rest.erp.equipment.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.equipment.rest.IErpEquipmentRest;
import com.skyeye.rest.erp.equipment.service.IErpEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IErpEquipmentServiceImpl
 * @Description: 设备信息
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IErpEquipmentServiceImpl extends IServiceImpl implements IErpEquipmentService {

    @Autowired
    private IErpEquipmentRest iErpEquipmentRest;


    @Override
    public List<Map<String, Object>>  queryLastMonthEquipmentCost() {
        return ExecuteFeignClient.get(() -> iErpEquipmentRest.queryLastMonthEquipmentCost()).getRows();
    }
}
