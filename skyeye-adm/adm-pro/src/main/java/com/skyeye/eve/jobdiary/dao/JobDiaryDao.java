/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.jobdiary.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.eve.jobdiary.entity.JobDiary;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: JobDiaryDao
 * @Description: 工作日志管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:59
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface JobDiaryDao extends SkyeyeBaseMapper<JobDiary> {

    @IgnoreTenant
    List<Map<String, Object>> queryMyReceivedJobDiaryList(CommonPageInfo commonPageInfo);

    @IgnoreTenant
    List<Map<String, Object>> queryMysendJobDiaryList(CommonPageInfo commonPageInfo);

}
