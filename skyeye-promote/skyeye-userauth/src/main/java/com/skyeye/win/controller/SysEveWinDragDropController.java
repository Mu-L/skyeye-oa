/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.win.controller;

import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.win.entity.SysEveUserCustomMenu;
import com.skyeye.win.entity.SysEveUserCustomMenubox;
import com.skyeye.win.service.SysEveUserCustomMenuService;
import com.skyeye.win.service.SysEveUserCustomMenuboxService;
import com.skyeye.win.service.SysEveWinDragDropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysEveWinDragDropController {

    @Autowired
    private SysEveWinDragDropService sysEveWinDragDropService;

    @Autowired
    private SysEveUserCustomMenuService sysEveUserCustomMenuService;

    @Autowired
    private SysEveUserCustomMenuboxService sysEveUserCustomMenuboxService;

    @ApiOperation(id = "sysevewindragdrop001", value = "用户自定义创建菜单盒子", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserCustomMenubox.class)
    @RequestMapping("/post/SysEveWinDragDropController/insertWinCustomMenuBox")
    public void insertWinCustomMenuBox(InputObject inputObject, OutputObject outputObject) {
        sysEveUserCustomMenuboxService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop002", value = "用户自定义创建菜单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserCustomMenu.class)
    @RequestMapping("/post/SysEveWinDragDropController/insertWinCustomMenu")
    public void insertWinCustomMenu(InputObject inputObject, OutputObject outputObject) {
        sysEveUserCustomMenuService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop003", value = "用户删除自定义菜单或文件夹", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveWinDragDropController/deleteWinMenuOrBoxById")
    public void deleteWinMenuOrBoxById(InputObject inputObject, OutputObject outputObject) {
        sysEveWinDragDropService.deleteWinMenuOrBoxById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop004", value = "用户自定义父菜单", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "菜单id", required = "required"),
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "菜单父id")})
    @RequestMapping("/post/SysEveWinDragDropController/editMenuParentIdById")
    public void editMenuParentIdById(InputObject inputObject, OutputObject outputObject) {
        sysEveWinDragDropService.editMenuParentIdById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop005", value = "获取菜单类型", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveWinDragDropController/queryMenuMationTypeById")
    public void queryMenuMationTypeById(InputObject inputObject, OutputObject outputObject) {
        sysEveWinDragDropService.queryMenuMationTypeById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop006", value = "根据id获取自定义盒子信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveWinDragDropController/queryCustomMenuBoxMationEditById")
    public void queryCustomMenuBoxMationEditById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserCustomMenuboxService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop007", value = "编辑自定义盒子", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserCustomMenubox.class)
    @RequestMapping("/post/SysEveWinDragDropController/editCustomMenuBoxMationById")
    public void editCustomMenuBoxMationById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserCustomMenuboxService.updateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop008", value = "编辑快捷方式时回显信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SysEveWinDragDropController/queryCustomMenuMationEditById")
    public void queryCustomMenuMationEditById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserCustomMenuService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "sysevewindragdrop009", value = "用户编辑自定义菜单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SysEveUserCustomMenu.class)
    @RequestMapping("/post/SysEveWinDragDropController/editCustomMenuMationById")
    public void editCustomMenuMationById(InputObject inputObject, OutputObject outputObject) {
        sysEveUserCustomMenuService.updateEntity(inputObject, outputObject);
    }

}
