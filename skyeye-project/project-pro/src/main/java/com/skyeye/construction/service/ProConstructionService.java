package com.skyeye.construction.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.construction.entity.ProConstruction;

/**
 * @ClassName: ProConstructionService
 * @Description: 施工方案Service层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProConstructionService extends SkyeyeBusinessService<ProConstruction> {

    void queryConstructionListByVersionNo(InputObject inputObject, OutputObject outputObject);

}