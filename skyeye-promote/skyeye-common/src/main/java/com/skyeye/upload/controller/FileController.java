/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.upload.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.upload.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FileController
 * @Description: 文件控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/12/25 11:57
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "文件管理", tags = "文件管理", modelName = "基础模块")
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation(id = "queryFileListByPath", value = "根据路径查询文件列表", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "path", name = "path", value = "文件路径，多个逗号隔开", required = "required")})
    @RequestMapping("/post/FileController/queryFileListByPath")
    public void queryFileListByPath(InputObject inputObject, OutputObject outputObject) {
        fileService.queryFileListByPath(inputObject, outputObject);
    }

}
