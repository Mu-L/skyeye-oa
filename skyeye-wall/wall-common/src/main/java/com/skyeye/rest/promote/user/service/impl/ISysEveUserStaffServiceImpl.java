/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.rest.promote.user.service.impl;

import com.skyeye.base.rest.service.impl.IServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.promote.user.rest.ISysEveUserStaffRest;
import com.skyeye.rest.promote.user.service.ISysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ISysEveUserStaffServiceImpl extends IServiceImpl implements ISysEveUserStaffService {

    @Autowired
    private ISysEveUserStaffRest iSysEveUserStaffRest;

    @Override
    public void updateTeacherWallBgImg(String backgroundImage) {
        Map<String, Object> map = new HashMap<>();
        map.put("backgroundImage", backgroundImage);
        ExecuteFeignClient.get(() -> iSysEveUserStaffRest.updateCurrentUserBgImg(map));
    }
}
