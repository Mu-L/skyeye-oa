/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.icon.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.icon.entity.SysEveIcon;
import com.skyeye.icon.service.SysEveIconService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveIconController
 * @Description: 系统icon库控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 21:38
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "系统icon库", tags = "系统icon库", modelName = "系统icon库")
public class SysEveIconController {

    @Autowired
    private SysEveIconService sysEveIconService;

    @ApiOperation(id = "querySysIconList", value = "获取ICON列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveIconController/querySysIconList")
    public void querySysIconList(InputObject inputObject, OutputObject outputObject) {
        sysEveIconService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSysIcon", value = "新增/编辑ICON信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysEveIcon.class)
    @RequestMapping("/post/SysEveIconController/writeSysIcon")
    public void writeSysIcon(InputObject inputObject, OutputObject outputObject) {
        sysEveIconService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySysIconById", value = "根据id查询icon信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveIconController/querySysIconById")
    public void querySysIconById(InputObject inputObject, OutputObject outputObject) {
        sysEveIconService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSysIconById", value = "删除ICON信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveIconController/deleteSysIconById")
    public void deleteSysIconById(InputObject inputObject, OutputObject outputObject) {
        sysEveIconService.deleteById(inputObject, outputObject);
    }

}
