/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.download.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.download.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DownloadController
 * @Description: 下载历史记录控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/27 12:10
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "下载历史", tags = "下载历史", modelName = "文档管理")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @ApiOperation(id = "queryDownloadList", value = "查询下载历史列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DownloadController/queryDownloadList")
    public void queryDownloadList(InputObject inputObject, OutputObject outputObject) {
        downloadService.queryPageList(inputObject, outputObject);
    }

}
