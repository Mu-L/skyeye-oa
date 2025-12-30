/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.arrangement.dao;

import com.skyeye.arrangement.entity.Arrangement;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArrangementDao
 * @Description: 面试安排数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/14 11:46
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ArrangementDao extends SkyeyeBaseMapper<Arrangement> {

    List<Map<String, Object>> queryBossInterviewArrangementList(CommonPageInfo commonPageInfo);

    List<Map<String, Object>> queryMyEntryBossPersonRequireAboutArrangementList(CommonPageInfo pageInfo);

    List<Map<String, Object>> queryArrangementInterviewerIsMyList(CommonPageInfo queryDo);

}
