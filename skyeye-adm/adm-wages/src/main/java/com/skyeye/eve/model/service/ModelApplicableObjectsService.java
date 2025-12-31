/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.model.entity.ModelApplicableObjects;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ModelApplicableObjectsService
 * @Description: 薪资模板适用对象服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 13:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface ModelApplicableObjectsService extends SkyeyeBusinessService<ModelApplicableObjects> {
    void deleteApplicableObjectsByPId(String modelId);

    void saveApplicableObjects(String modelId, List<ModelApplicableObjects> applicableObjectsList);

    List<ModelApplicableObjects> queryApplicableObjectsByPId(String modelId);

    Map<String, List<ModelApplicableObjects>> queryApplicableObjectsByPId(List<String> modelId);
}
