/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service;

import com.skyeye.afterseal.entity.SealFaultUseMaterial;
import com.skyeye.base.business.service.SkyeyeLinkDataService;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealFaultUseMaterialService
 * @Description: 售后服务故障配件使用信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/12 21:37
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface SealFaultUseMaterialService extends SkyeyeLinkDataService<SealFaultUseMaterial> {

    /**
     * 计算单据信息的总价
     *
     * @param sealFaultUseMaterials
     * @return
     */
    String calcOrderAllTotalPrice(List<SealFaultUseMaterial> sealFaultUseMaterials);

    /**
     * 配件使用数量统计
     */
    Long queryUseCount(String startTime, String endTime);

    Map<String, Long> queryUseCountByUserId(List<String> userIdList, String startTime, String endTime);

}
