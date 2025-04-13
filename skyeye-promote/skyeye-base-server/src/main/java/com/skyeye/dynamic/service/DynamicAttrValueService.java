/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dynamic.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.dynamic.entity.DynamicAttrValue;

/**
 * @ClassName: DynamicAttrValueService
 * @Description: 动态属性值服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/13 14:14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DynamicAttrValueService extends SkyeyeBusinessService<DynamicAttrValue> {

    void writeDynamicAttrValue(InputObject inputObject, OutputObject outputObject);

    void writeBatchDynamicAttrValue(InputObject inputObject, OutputObject outputObject);

    void queryDynamicAttrValueList(InputObject inputObject, OutputObject outputObject);

    void queryBatchDynamicAttrValueList(InputObject inputObject, OutputObject outputObject);

    void deleteDynamicAttrValue(InputObject inputObject, OutputObject outputObject);

    void deleteBatchDynamicAttrValue(InputObject inputObject, OutputObject outputObject);
}
