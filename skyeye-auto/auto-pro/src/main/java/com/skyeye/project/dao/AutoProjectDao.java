/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.dao;

import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.project.entity.AutoProject;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoProjectDao
 * @Description: 项目管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:27
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AutoProjectDao extends SkyeyeBaseMapper<AutoProject> {

    List<Map<String, Object>> queryAutoProjectList(CommonPageInfo projectQueryDo);

}
