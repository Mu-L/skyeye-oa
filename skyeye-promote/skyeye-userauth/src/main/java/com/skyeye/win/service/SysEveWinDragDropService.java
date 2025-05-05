/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

import java.util.List;
import java.util.Map;

public interface SysEveWinDragDropService {

    void deleteWinMenuOrBoxById(InputObject inputObject, OutputObject outputObject);

    void editMenuParentIdById(InputObject inputObject, OutputObject outputObject);

    void queryMenuMationTypeById(InputObject inputObject, OutputObject outputObject);

    List<Map<String, Object>> queryCustomDeskTopsMenuByUserId(String userId);

}
