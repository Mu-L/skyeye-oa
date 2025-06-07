/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.organization.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.organization.entity.JobScore;
import com.skyeye.organization.entity.JobScoreQueryDo;

import java.util.List;
import java.util.Map;

public interface CompanyJobScoreDao extends SkyeyeBaseMapper<JobScore> {

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyJobScoreList(JobScoreQueryDo pageInfo);

    @IgnoreTenant
    List<Map<String, Object>> queryEnableCompanyJobScoreList(Map<String, Object> map);
}
