/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.product.entity.ProductLeadChild;
import com.skyeye.production.entity.ProductionChild;

import java.util.List;

/**
 * @ClassName: ProductionChildService
 * @Description: 生产计划单子单据服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/28 21:23
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ProductionChildService extends SkyeyeBusinessService<ProductionChild> {

    void deleteByParentId(String parentId);

    List<ProductionChild> selectByParentId(String parentId);

    List<ProductionChild> selectByParentId(List<String> parentIds);

    void saveList(String parentId, List<ProductionChild> productionChildList);
}
