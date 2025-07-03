/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysEveWinDragDropDao {

    @IgnoreTenant
    Map<String, Object> queryMenuMationFromSysById(Map<String, Object> map);

    @IgnoreTenant
    Map<String, Object> queryMenuMationTypeById(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> queryCustomDeskTopsMenuByUserId(@Param("userId") String userId, @Param("tenantId") String tenantId);

}
