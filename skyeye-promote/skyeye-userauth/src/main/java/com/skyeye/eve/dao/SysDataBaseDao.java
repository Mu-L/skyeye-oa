/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;

import java.util.List;
import java.util.Map;

public interface SysDataBaseDao {

    @IgnoreTenant
    List<Map<String, Object>> querySysDataBaseSelectList(Map<String, Object> map);

    @IgnoreTenant
    List<Map<String, Object>> querySysDataBaseDescSelectList(Map<String, Object> map);

}
