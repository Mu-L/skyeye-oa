/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.win.entity.SysEveWinThemeColor;
import com.skyeye.win.service.SysEveWinThemeColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveWinThemeColorController
 * @Description: win系统主题颜色控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/22 12:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "win系统主题颜色", tags = "win系统主题颜色", modelName = "win系统主题颜色")
public class SysEveWinThemeColorController {

    @Autowired
    private SysEveWinThemeColorService sysEveWinThemeColorService;

    /**
     * 获取win系统主题颜色列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySysEveWinThemeColorList", value = "获取win系统主题颜色列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveWinThemeColorController/querySysEveWinThemeColorList")
    public void querySysEveWinThemeColorList(InputObject inputObject, OutputObject outputObject) {
        sysEveWinThemeColorService.queryPageList(inputObject, outputObject);
    }

    /**
     * 添加/编辑win系统主题颜色信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSysEveWinThemeColor", value = "添加/编辑win系统主题颜色信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysEveWinThemeColor.class)
    @RequestMapping("/post/SysEveWinThemeColorController/writeSysEveWinThemeColor")
    public void writeSysEveWinThemeColor(InputObject inputObject, OutputObject outputObject) {
        sysEveWinThemeColorService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除win系统主题颜色信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSysEveWinThemeColorById", value = "删除win系统主题颜色信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveWinThemeColorController/deleteSysEveWinThemeColorById")
    public void deleteSysEveWinThemeColorById(InputObject inputObject, OutputObject outputObject) {
        sysEveWinThemeColorService.deleteById(inputObject, outputObject);
    }

}
