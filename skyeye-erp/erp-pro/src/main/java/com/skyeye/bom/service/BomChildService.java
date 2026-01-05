/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.bom.entity.BomChild;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: BomChildService
 * @Description: bom表子件清单服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/27 14:46
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface BomChildService extends SkyeyeBusinessService<BomChild> {

    void deleteBomChildByBomId(String bomId);

    List<BomChild> queryBomChildByBomId(String bomId);

    Map<String, List<BomChild>> queryBomChildByBomId(List<String> bomIds);

}
