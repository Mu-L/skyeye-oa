/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.wages.service.impl;

import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.wages.rest.IWagesRest;
import com.skyeye.rest.wages.service.IWagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: IWagesServiceImpl
 * @Description: 薪资服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/1 10:52
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IWagesServiceImpl implements IWagesService {

    @Autowired
    private IWagesRest iWagesRest;

    @Override
    public void addWagesStaffMationByStaffId(String staffId) {
        ExecuteFeignClient.get(() -> iWagesRest.addWagesStaffMationByStaffId(staffId));
    }

}
