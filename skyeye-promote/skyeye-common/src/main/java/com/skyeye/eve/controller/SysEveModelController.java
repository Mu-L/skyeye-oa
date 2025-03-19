/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.model.SysEveModel;
import com.skyeye.eve.service.SysEveModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveModelController
 * @Description: 素材管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/6/30 22:26
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "素材管理", tags = "素材管理", modelName = "素材管理")
public class SysEveModelController {

    @Autowired
    private SysEveModelService sysEveModelService;

    @ApiOperation(id = "sysevemodel001", value = "获取素材列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveModelController/querySysEveModelList")
    public void querySysEveModelList(InputObject inputObject, OutputObject outputObject) {
        sysEveModelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodel002", value = "新增素材", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysEveModel.class)
    @RequestMapping("/post/SysEveModelController/insertSysEveModelMation")
    public void insertSysEveModelMation(InputObject inputObject, OutputObject outputObject) {
        sysEveModelService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodel003", value = "删除素材", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveModelController/deleteSysEveModelById")
    public void deleteSysEveModelById(InputObject inputObject, OutputObject outputObject) {
        sysEveModelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodel004", value = "根据id查询素材详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveModelController/selectSysEveModelById")
    public void selectSysEveModelById(InputObject inputObject, OutputObject outputObject) {
        sysEveModelService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodel005", value = "编辑素材", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = SysEveModel.class)
    @RequestMapping("/post/SysEveModelController/editSysEveModelMationById")
    public void editSysEveModelMationById(InputObject inputObject, OutputObject outputObject) {
        sysEveModelService.updateEntity(inputObject, outputObject);
    }

}
