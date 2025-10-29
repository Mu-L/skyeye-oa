/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.diskcloud.entity.FileShare;
import com.skyeye.eve.diskcloud.service.FileShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FileShareController
 * @Description: 文件分享控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/18 11:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "文件分享管理", tags = "文件分享管理", modelName = "文件分享管理")
public class FileShareController {

    @Autowired
    private FileShareService fileShareService;

    @ApiOperation(id = "insertFileToShare", value = "文件分享", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = FileShare.class)
    @RequestMapping("/post/FileShareController/insertFileToShare")
    public void insertFileToShare(InputObject inputObject, OutputObject outputObject) {
        fileShareService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShareFileList", value = "我的文件分享列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FileShareController/queryShareFileList")
    public void queryShareFileList(InputObject inputObject, OutputObject outputObject) {
        fileShareService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteShareFileById", value = "删除文件分享外链", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileShareController/deleteShareFileById")
    public void deleteShareFileById(InputObject inputObject, OutputObject outputObject) {
        fileShareService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryShareFileMationById", value = "文件共享输入密码时获取文件信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileShareController/queryShareFileMationById")
    public void queryShareFileMationById(InputObject inputObject, OutputObject outputObject) {
        fileShareService.queryShareFileMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole020", value = "文件共享输入密码时校验", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "sharePassword", name = "sharePassword", value = "密码", required = "required")})
    @RequestMapping("/post/FileShareController/checkShareFilePwdMation")
    public void checkShareFilePwdMation(InputObject inputObject, OutputObject outputObject) {
        fileShareService.checkShareFilePwdMation(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole021", value = "获取分享文件基础信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileShareController/queryShareFileBaseMationById")
    public void queryShareFileBaseMationById(InputObject inputObject, OutputObject outputObject) {
        fileShareService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole022", value = "根据父id获取该id下分享的文件和文件夹", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileShareController/queryShareFileListByParentId")
    public void queryShareFileListByParentId(InputObject inputObject, OutputObject outputObject) {
        fileShareService.queryShareFileListByParentId(inputObject, outputObject);
    }

}
