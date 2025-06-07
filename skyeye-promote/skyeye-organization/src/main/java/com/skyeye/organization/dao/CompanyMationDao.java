/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.organization.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.eve.dao.SkyeyeBaseMapper;
import com.skyeye.organization.entity.Company;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyMationDao
 * @Description: 企业管理数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/22 16:05
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface CompanyMationDao extends SkyeyeBaseMapper<Company> {

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyMationList(Map<String, Object> map);

    @IgnoreTenant
    Map<String, Object> queryCompanyMationById(@Param("id") String id,
                                               @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyMationListTree(Map<String, Object> map);

    @IgnoreTenant
    Map<String, Object> queryCompanyUserNumMationById(@Param("id") String id,
                                                      @Param("tenantId") String tenantId);

    @IgnoreTenant
    Map<String, Object> queryCompanyDepartMentNumMationById(@Param("id") String id,
                                                            @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyListToSelect(@Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCompanyList(@Param("id") String id,
                                               @Param("tenantId") String tenantId);
}
