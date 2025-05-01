/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.field.entity.FieldType;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WagesFieldTypeService
 * @Description: 薪资字段管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WagesFieldTypeService extends SkyeyeBusinessService<FieldType> {

    void queryEnableWagesFieldTypeList(InputObject inputObject, OutputObject outputObject);

    void querySysWagesFieldTypeList(InputObject inputObject, OutputObject outputObject);

    void queryWagesFieldListByKeys(InputObject inputObject, OutputObject outputObject);

    Map<String, FieldType> queryAllFieldTypeMap();

    List<FieldType> queryAllWagesFieldTypeList();
}
