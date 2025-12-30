/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.appeal.dao;

import com.skyeye.appeal.entity.Appeal;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AppealDao
 * @Description: 考勤申诉管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/18 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AppealDao extends SkyeyeBaseMapper<Appeal> {

    List<Map<String, Object>> queryAppealList(CommonPageInfo pageInfo);

}
