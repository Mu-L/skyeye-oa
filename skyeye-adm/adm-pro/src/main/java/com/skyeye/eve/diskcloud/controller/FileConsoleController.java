/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.diskcloud.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.diskcloud.service.FileConsoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SysWorkPlanController
 * @Description: 文件管理--云盘
 * @author: skyeye云系列--卫志强
 * @date: 2022/6/30 22:26
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "文件管理", tags = "文件管理", modelName = "文件管理")
public class FileConsoleController {

    @Autowired
    private FileConsoleService fileConsoleService;

    @ApiOperation(id = "fileconsole001", value = "根据当前用户获取目录", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父目录id，默认为0", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryFileFolderByUserId")
    public void queryFileFolderByUserId(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryFileFolderByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole003", value = "获取这个目录下的所有文件+目录", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "orderBy", name = "orderBy", value = "排序方式"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryFilesListByFolderId")
    public void queryFilesListByFolderId(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryFilesListByFolderId(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole004", value = "删除目录以及目录下的所有文件", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "fileList", name = "fileList", value = "要删除的目录id集合,包含 id,fileType", required = "required,json")})
    @RequestMapping("/post/FileConsoleController/deleteFileFolderById")
    public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.deleteFileFolderById(inputObject, outputObject);
    }

    @ApiOperation(id = "editCloudFileFolderById", value = "编辑文件夹或者文件的名称", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "文件夹/文件id", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "文件夹/文件的名称", required = "required"),
        @ApiImplicitParam(id = "fileType", name = "fileType", value = "文件或者文件夹类型", required = "required")})
    @RequestMapping("/post/FileConsoleController/editCloudFileFolderById")
    public void editCloudFileFolderById(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.editFileFolderById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertUploadFile", value = "上传文件", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "文件名", required = "required"),
        @ApiImplicitParam(id = "size", name = "size", value = "文件大小", required = "required,num"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件所属目录id", required = "required"),
        @ApiImplicitParam(id = "md5", name = "md5", value = "文件唯一标示", required = "required"),
        @ApiImplicitParam(id = "chunk", name = "chunk", value = "分块上传，块下标", required = "required"),
        @ApiImplicitParam(id = "chunkSize", name = "chunkSize", value = "分块上传时，块的大小，用于最后合并", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertUploadFile")
    public void insertUploadFile(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertUploadFile(inputObject, outputObject);
    }

    @ApiOperation(id = "insertUploadFileChunks", value = "上传文件合并块", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "name", name = "name", value = "文件名", required = "required"),
        @ApiImplicitParam(id = "size", name = "size", value = "文件大小", required = "required,num"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件所属目录id", required = "required"),
        @ApiImplicitParam(id = "md5", name = "md5", value = "文件唯一标示", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertUploadFileChunks")
    public void insertUploadFileChunks(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertUploadFileChunks(inputObject, outputObject);
    }

    @ApiOperation(id = "queryUploadFileChunksByChunkMd5", value = "文件分块上传检测是否上传", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "md5", name = "md5", value = "文件唯一标示", required = "required"),
        @ApiImplicitParam(id = "chunk", name = "chunk", value = "分块上传，块下标", required = "required"),
        @ApiImplicitParam(id = "chunkSize", name = "chunkSize", value = "分块上传时，块的大小，用于最后合并", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryUploadFileChunksByChunkMd5")
    public void queryUploadFileChunksByChunkMd5(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryUploadFileChunksByChunkMd5(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFileConsoleById", value = "根据id查询文件信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryFileConsoleById")
    public void queryFileConsoleById(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole010", value = "office文件编辑", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "key", name = "key", value = "定义服务用于文档识别的唯一文档标识符", required = "required"),
        @ApiImplicitParam(id = "url", name = "url", value = "定义存储源查看或编辑文档的绝对URL", required = "required"),
        @ApiImplicitParam(id = "status", name = "status", value = "状态", required = "required")})
    @RequestMapping(value = "/post/FileConsoleController/editUploadOfficeFileById", method = RequestMethod.POST)
    public void editUploadOfficeFileById(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.editUploadOfficeFileById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllFileSizeByUserId", value = "根据当前用户获取总文件大小", method = "GET", allUse = "2")
    @RequestMapping("/post/FileConsoleController/queryAllFileSizeByUserId")
    public void queryAllFileSizeByUserId(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryAllFileSizeByUserId(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole023", value = "分享文件保存", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "jsonStr", name = "jsonStr", value = "保存的文件数组", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertShareFileListToSave")
    public void insertShareFileListToSave(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertShareFileListToSave(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole024", value = "文档在线预览", method = "GET", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryFileToShowById")
    public void queryFileToShowById(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryFileToShowById(inputObject, outputObject);
    }

    @ApiOperation(id = "createFileToService", value = "创建空文件", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "type", name = "type", value = "文件类型", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/createFileToService")
    public void createFileToService(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.createFileToService(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole030", value = "创建副本", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "jsonStr", name = "jsonStr", value = "保存的文件数组", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertDuplicateCopyToService")
    public void insertDuplicateCopyToService(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertDuplicateCopyToService(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole031", value = "获取文件属性", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryFileMationById")
    public void queryFileMationById(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryFileMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole032", value = "文件打包", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "jsonStr", name = "jsonStr", value = "保存的文件数组", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertFileMationToPackageToFolder")
    public void insertFileMationToPackageToFolder(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertFileMationToPackageToFolder(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole033", value = "压缩包解压", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertFileMationPackageToFolder")
    public void insertFileMationPackageToFolder(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertFileMationPackageToFolder(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole034", value = "文件或者文件夹复制", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "jsonStr", name = "jsonStr", value = "保存的文件数组", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertPasteCopyToService")
    public void insertPasteCopyToService(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertPasteCopyToService(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole035", value = "文件或者文件夹剪切", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "jsonStr", name = "jsonStr", value = "保存的文件数组", required = "required"),
        @ApiImplicitParam(id = "folderId", name = "folderId", value = "文件夹id", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertPasteCutToService")
    public void insertPasteCutToService(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertPasteCutToService(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole036", value = "office文件编辑获取修改时间作为最新的key", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FileConsoleController/queryOfficeUpdateTimeToKey")
    public void queryOfficeUpdateTimeToKey(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryOfficeUpdateTimeToKey(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole037", value = "文件统计报表", method = "GET", allUse = "2")
    @RequestMapping("/post/FileConsoleController/queryFileNumStatistics")
    public void queryFileNumStatistics(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.queryFileNumStatistics(inputObject, outputObject);
    }

    @ApiOperation(id = "fileconsole038", value = "文件打包下载", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "jsonStr", name = "jsonStr", value = "保存的文件数组", required = "required")})
    @RequestMapping("/post/FileConsoleController/insertFileMationToPackageDownload")
    public void insertFileMationToPackageDownload(InputObject inputObject, OutputObject outputObject) {
        fileConsoleService.insertFileMationToPackageDownload(inputObject, outputObject);
    }

}
