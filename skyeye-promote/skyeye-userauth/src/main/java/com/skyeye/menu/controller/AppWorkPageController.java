/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.menu.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.menu.entity.AppWorkPage;
import com.skyeye.menu.service.AppWorkPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppWorkPageServiceImpl
 * @Description: 手机端菜单以及目录功能控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 23:18
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "手机端菜单管理", tags = "手机端菜单管理", modelName = "菜单管理")
public class AppWorkPageController {

    @Autowired
    private AppWorkPageService appWorkPageService;

    @ApiOperation(id = "queryAppWorkPageList", value = "获取菜单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AppWorkPageController/queryAppWorkPageList")
    public void queryAppWorkPageList(InputObject inputObject, OutputObject outputObject) {
        appWorkPageService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAppWorkPageMation", value = "新增/编辑手机端菜单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AppWorkPage.class)
    @RequestMapping("/post/AppWorkPageController/writeAppWorkPageMation")
    public void writeAppWorkPageMation(InputObject inputObject, OutputObject outputObject) {
        appWorkPageService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAppWorkPageById", value = "根据id查询菜单", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppWorkPageController/queryAppWorkPageById")
    public void queryAppWorkPageById(InputObject inputObject, OutputObject outputObject) {
        appWorkPageService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAppWorkPageById", value = "删除菜单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppWorkPageController/deleteAppWorkPageById")
    public void deleteAppWorkPageById(InputObject inputObject, OutputObject outputObject) {
        appWorkPageService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAppWorkPageListByDesktopId", value = "根据桌面id获取目录集合", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "desktopId", name = "desktopId", value = "桌面id")})
    @RequestMapping("/post/AppWorkPageController/queryAppWorkPageListByDesktopId")
    public void queryAppWorkPageListByDesktopId(InputObject inputObject, OutputObject outputObject) {
        appWorkPageService.queryAppWorkPageListByDesktopId(inputObject, outputObject);
    }

}
