/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.model.SysEveModelType;
import com.skyeye.eve.service.SysEveModelTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysEveModelTypeController
 * @Description: 素材分类管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/6 9:03
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "素材分类管理", tags = "素材分类管理", modelName = "素材管理")
public class SysEveModelTypeController {

    @Autowired
    private SysEveModelTypeService sysEveModelTypeService;

    @ApiOperation(id = "sysevemodeltype001", value = "获取系统模板分类列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveModelTypeController/querySysEveModelTypeList")
    public void querySysEveModelTypeList(InputObject inputObject, OutputObject outputObject) {
        sysEveModelTypeService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodeltype002", value = "新增系统模板分类", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SysEveModelType.class)
    @RequestMapping("/post/SysEveModelTypeController/insertSysEveModelType")
    public void insertSysEveModelType(InputObject inputObject, OutputObject outputObject) {
        sysEveModelTypeService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodeltype003", value = "删除系统模板分类", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveModelTypeController/delSysEveModelTypeById")
    public void delSysEveModelTypeById(InputObject inputObject, OutputObject outputObject) {
        sysEveModelTypeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodeltype004", value = "根据id查询系统模板分类详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveModelTypeController/querySysEveModelTypeById")
    public void querySysEveModelTypeById(InputObject inputObject, OutputObject outputObject) {
        sysEveModelTypeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodeltype005", value = "编辑系统模板分类", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = SysEveModelType.class)
    @RequestMapping("/post/SysEveModelTypeController/updateSysEveModelTypeById")
    public void updateSysEveModelTypeById(InputObject inputObject, OutputObject outputObject) {
        sysEveModelTypeService.updateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevemodeltype006", value = "通过parentId查找对应的系统模板分类列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父id", required = "required")})
    @RequestMapping("/post/SysEveModelTypeController/querySysEveModelTypeByParentId")
    public void querySysEveModelTypeByParentId(InputObject inputObject, OutputObject outputObject) {
        sysEveModelTypeService.querySysEveModelTypeByParentId(inputObject, outputObject);
    }
}
