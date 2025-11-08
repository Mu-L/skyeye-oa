/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.note.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.note.entity.Note;
import com.skyeye.eve.note.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: NoteController
 * @Description: 笔记管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/25 19:19
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "笔记管理", tags = "笔记管理", modelName = "笔记管理")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @ApiOperation(id = "deleteFileFolderById", value = "删除文件夹或笔记", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "文件夹/笔记id", required = "required"),
        @ApiImplicitParam(id = "fileType", name = "fileType", value = "笔记或者文件夹类型", required = "required")})
    @RequestMapping("/post/NoteController/deleteFileFolderById")
    public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) {
        noteService.deleteFileFolderById(inputObject, outputObject);
    }

    @ApiOperation(id = "editFileFolderById", value = "编辑文件夹或者笔记的名称", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "文件夹/笔记id", required = "required"),
        @ApiImplicitParam(id = "name", name = "name", value = "文件夹/笔记id的名称", required = "required"),
        @ApiImplicitParam(id = "fileType", name = "fileType", value = "笔记或者文件夹类型", required = "required")})
    @RequestMapping("/post/NoteController/editFileFolderById")
    public void editFileFolderById(InputObject inputObject, OutputObject outputObject) {
        noteService.editFileFolderById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNewNoteListByUserId", value = "获取当前用户最新的笔记列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/NoteController/queryNewNoteListByUserId")
    public void queryNewNoteListByUserId(InputObject inputObject, OutputObject outputObject) {
        noteService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeNote", value = "新增/编辑笔记", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Note.class)
    @RequestMapping("/post/NoteController/writeNote")
    public void writeNote(InputObject inputObject, OutputObject outputObject) {
        noteService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "mynote006", value = "根据文件夹id获取文件夹下的文件夹和笔记列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "文件夹id", required = "required"),
        @ApiImplicitParam(id = "search", name = "search", value = "搜索框的值")})
    @RequestMapping("/post/MyNoteController/queryFileAndContentListByFolderId")
    public void queryFileAndContentListByFolderId(InputObject inputObject, OutputObject outputObject) {
        noteService.queryFileAndContentListByFolderId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNoteById", value = "根据id获取笔记信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "笔记id", required = "required")})
    @RequestMapping("/post/NoteController/queryNoteById")
    public void queryNoteById(InputObject inputObject, OutputObject outputObject) {
        noteService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "mynote010", value = "保存文件夹拖拽后的信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "targetId", name = "targetId", value = "拖拽目标节点id", required = "required"),
        @ApiImplicitParam(id = "arrId", name = "arrId", value = "拖拽节点id数组", required = "required")})
    @RequestMapping("/post/NoteController/editFileToDragById")
    public void editFileToDragById(InputObject inputObject, OutputObject outputObject) {
        noteService.editFileToDragById(inputObject, outputObject);
    }

    @ApiOperation(id = "mynote011", value = "保存笔记移动后的信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "toId", name = "toId", value = "移动目标节点id", required = "required"),
        @ApiImplicitParam(id = "moveId", name = "moveId", value = "移动笔记id", required = "required")})
    @RequestMapping("/post/NoteController/editNoteToMoveById")
    public void editNoteToMoveById(InputObject inputObject, OutputObject outputObject) {
        noteService.editNoteToMoveById(inputObject, outputObject);
    }

    @ApiOperation(id = "outputNoteIsZipJob", value = "根据id(文件夹或者笔记id)将笔记输出为压缩包", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "文件夹/文件id", required = "required"),
        @ApiImplicitParam(id = "type", name = "type", value = "类型", required = "required")})
    @RequestMapping("/post/NoteController/outputNoteIsZipJob")
    public void outputNoteIsZipJob(InputObject inputObject, OutputObject outputObject) {
        noteService.outputNoteIsZipJob(inputObject, outputObject);
    }

}
