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
import com.skyeye.win.entity.SysEveWinBgPic;
import com.skyeye.win.service.SysEveWinBgPicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveWinBgPicController
 * @Description: win系统桌面图片控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 22:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "win系统桌面图片", tags = "win系统桌面图片", modelName = "win系统桌面图片")
public class SysEveWinBgPicController {

    @Autowired
    private SysEveWinBgPicService sysEveWinBgPicService;

    @ApiOperation(id = "querySysEveWinBgPicList", value = "获取win系统桌面图片列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveWinBgPicController/querySysEveWinBgPicList")
    public void querySysEveWinBgPicList(InputObject inputObject, OutputObject outputObject) {
        sysEveWinBgPicService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertSysEveWinBgPic", value = "添加win系统桌面图片信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveWinBgPic.class)
    @RequestMapping("/post/SysEveWinBgPicController/insertSysEveWinBgPic")
    public void insertSysEveWinBgPic(InputObject inputObject, OutputObject outputObject) {
        sysEveWinBgPicService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSysEveWinBgPicById", value = "删除win系统桌面图片信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveWinBgPicController/deleteSysEveWinBgPicById")
    public void deleteSysEveWinBgPicById(InputObject inputObject, OutputObject outputObject) {
        sysEveWinBgPicService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySysEveWinBgPicCustomList", value = "获取用户自定义的win系统桌面图片列表", method = "POST", allUse = "2")
    @RequestMapping("/post/SysEveWinBgPicController/querySysEveWinBgPicCustomList")
    public void querySysEveWinBgPicCustomList(InputObject inputObject, OutputObject outputObject) {
        sysEveWinBgPicService.querySysEveWinBgPicCustomList(inputObject, outputObject);
    }

}
