/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.role.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.role.entity.Role;
import com.skyeye.role.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ShopDeliveryCompanyController
 * @Description: ai角色控制类
 * @author: skyeye云系列--卫志强
 * @date: 2024/10/8 10:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "AI角色", tags = "AI角色", modelName = "AI角色")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation(id = "writeRole", value = "新增/编辑AI角色", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Role.class)
    @RequestMapping("/post/roleController/writeRole")
    public void writeRole(InputObject inputObject, OutputObject outputObject) {
        roleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRolePageList", value = "分页查询AI角色", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/roleController/queryRole")
    public void queryRole(InputObject inputObject, OutputObject outputObject) {
        roleService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteRoleById", value = "删除AI角色", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id列表", required = "required")})
    @RequestMapping("/post/roleController/deleteRoleByIds")
    public void deleteRoleByIds(InputObject inputObject, OutputObject outputObject) {
        roleService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "selectRoleById", value = "根据id获取AI角色", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/roleController/selectRoleById")
    public void selectRoleById(InputObject inputObject, OutputObject outputObject) {
        roleService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryRoleList", value = "获取全部Ai角色", method = "POST", allUse = "2")
    @RequestMapping("/post/roleController/queryRoleList")
    public void queryRoleList(InputObject inputObject, OutputObject outputObject) {
        roleService.queryList(inputObject, outputObject);
    }
}
