/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.supplier.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.supplier.entity.Supplier;

/**
 * @ClassName: SupplierService
 * @Description: 供应商信息管理服务接口类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SupplierService extends SkyeyeBusinessService<Supplier> {

    void querySupplierList(InputObject inputObject, OutputObject outputObject);

    void queryAllSupplierList(InputObject inputObject, OutputObject outputObject);

}
