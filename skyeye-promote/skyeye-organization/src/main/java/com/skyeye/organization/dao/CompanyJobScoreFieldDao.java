/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.organization.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.organization.entity.JobScoreField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CompanyJobScoreFieldDao extends SkyeyeBaseMapper<JobScoreField> {

    @IgnoreTenant
    int deleteCompanyJobScoreFieldByJobScoreId(@Param("jobScoreIdList") List<String> jobScoreIdList,
                                               @Param("tenantId") String tenantId);

}
