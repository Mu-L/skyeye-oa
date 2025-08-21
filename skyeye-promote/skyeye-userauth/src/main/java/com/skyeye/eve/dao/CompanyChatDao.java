/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CompanyChatDao {

    @IgnoreTenant
    Map<String, Object> queryUserMineByUserId(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryDepartmentUserByDepartId(@Param("departIds") List<String> departIds,
                                                            @Param("notInUserIds") List<String> notInUserIds,
                                                            @Param("tenantId") String tenantId);

    @IgnoreTenant
    List<Map<String, Object>> queryUserGroupByUserId(Map<String, Object> map);

    @IgnoreTenant
    int editUserSignByUserId(Map<String, Object> map);

}
