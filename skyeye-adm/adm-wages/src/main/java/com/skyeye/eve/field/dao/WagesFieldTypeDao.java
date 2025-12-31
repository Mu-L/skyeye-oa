/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.field.entity.FieldType;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: WagesFieldTypeDao
 * @Description: 薪资字段管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface WagesFieldTypeDao extends SkyeyeBaseMapper<FieldType> {

    List<Map<String, Object>> queryWagesFieldTypeList(CommonPageInfo pageInfo);

    List<Map<String, Object>> queryAllStaffMationList();

}
