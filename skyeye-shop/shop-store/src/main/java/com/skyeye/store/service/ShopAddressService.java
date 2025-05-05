/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.ShopAddress;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopAddressService
 * @Description: 收件地址管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/4 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ShopAddressService extends SkyeyeBusinessService<ShopAddress> {
    void queryDefaultShopAddress(InputObject inputObject, OutputObject outputObject);

    Map<String,Map<String, Object>> queryListByIds(List<String> addressTableIdList);
}
