/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.statis.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CrmPageDao {

    @IgnoreTenant
    List<Map<String, Object>> queryInsertNumByYear(@Param("year") String year,
                                                   @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCustomNumByType(@Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCustomNumByFrom(@Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCustomNumByIndustry(@Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCustomNumByGroup(@Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryCustomDocumentaryType(@Param("year") String year,
                                                         @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryNewContractNum(@Param("year") String year,
                                                  @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryNewDocumentaryNum(@Param("year") String year,
                                                     @Param("tenantId") String tenantId);

}
