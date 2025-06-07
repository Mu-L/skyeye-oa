/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.organization.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.organization.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyDepartmentDao
 * @Description: 企业部门信息管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/30 19:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface CompanyDepartmentDao extends SkyeyeBaseMapper<Department> {

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyDepartmentList(CommonPageInfo commonPageInfo);

    @IgnoreTenant
    Map<String, Object> queryCompanyDepartmentUserMationById(@Param("id") String id,
                                                             @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyDepartmentListTreeByCompanyId(Map<String, Object> map);

    @IgnoreTenant
    Map<String, Object> queryCompanyJobNumMationById(@Param("id") String id,
                                                     @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyDepartmentOrganization(@Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryDepartmentList(@Param("companyIds") List<String> companyIds,
                                                  @Param("departmentIds") List<String> departmentIds,
                                                  @Param("tenantId") String tenantId);
}
