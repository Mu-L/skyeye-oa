/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ueditor.controller;

import com.alibaba.fastjson.JSONObject;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.ueditor.service.EditUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(value = "富文本编辑器上传、下载接口", tags = "富文本编辑器上传、下载接口", modelName = "富文本编辑器")
public class EditUploadController {

    @Autowired
    private EditUploadService editUploadService;

    /**
     * 上传富文本文件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "editorUploadContentPic", value = "上传文件", method = "POST", allUse = "2")
    @RequestMapping("/post/EditUploadController/uploadContentPic")
    public void uploadContentPic(InputObject inputObject, OutputObject outputObject) {
        editUploadService.uploadContentPic(inputObject, outputObject);
    }

    /**
     * 回显富文本图片
     *
     * @param req
     * @param callback
     * @return
     */
    @RequestMapping("/upload/editUploadController/downloadContentPic")
    public String downloadContentPic(HttpServletRequest req, @RequestParam("callback") String callback, @RequestParam("userId") String userId) {
        return callback + "(" + JSONObject.toJSONString(editUploadService.downloadContentPic(req, userId)) + ")";
    }

    @RequestMapping("/upload/editUploadController/ueeditorConif")
    public String ueeditorConif(HttpServletRequest request, @RequestParam("callback") String callback,
                                @RequestParam("fileBasePath") String fileBasePath) {
        return callback + "(" + PublicMsg.getUeditorConfig(fileBasePath) + ")";
    }

}
