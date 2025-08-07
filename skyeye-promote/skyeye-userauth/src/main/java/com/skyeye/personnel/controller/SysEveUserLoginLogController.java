/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.service.SysEveUserLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveUserLoginLogController
 * @Description: 用户登录日志控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 20:50
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用户登录日志", tags = "用户登录日志", modelName = "用户管理")
public class SysEveUserLoginLogController {

    @Autowired
    private SysEveUserLoginLogService sysEveUserLoginLogService;

    @ApiOperation(id = "queryLoginLogList", value = "查询登录日志列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveUserLoginLogController/queryLoginLogList")
    public void queryLoginLogList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserLoginLogService.queryPageList(inputObject, outputObject);
    }

}