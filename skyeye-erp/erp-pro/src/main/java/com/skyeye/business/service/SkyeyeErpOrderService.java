/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.business.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SkyeyeErpOrderService
 * @Description: ERP单据的service接口
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/24 20:22
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SkyeyeErpOrderService<T> extends SkyeyeBusinessService<T> {

    /**
     * 获取指定单据id已经(审批通过)的产品的数量
     *
     * @param fromId 来源单据id
     * @return
     */
    Map<String, Integer> calcMaterialNormsNumByFromId(String fromId);

    void setOrderMationByFromId(List<Map<String, Object>> beans, String idKey, String mationKey);

    void editOtherState(String id, Integer otherState);

}
