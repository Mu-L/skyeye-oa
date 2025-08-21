/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.member.service.DocMemberLoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocMemberLoginLogController
 * @Description: 文档会员登录日志控制器
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/21 8:55
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "文档会员登录日志", tags = "文档会员登录日志", modelName = "会员管理")
public class DocMemberLoginLogController {

    @Autowired
    private DocMemberLoginLogService docMemberLoginLogService;

    @ApiOperation(id = "queryDocMemberLoginLogList", value = "查询文档会员登录日志列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DocMemberLoginLogController/queryDocMemberLoginLogList")
    public void queryDocMemberLoginLogList(InputObject inputObject, OutputObject outputObject) {
        docMemberLoginLogService.queryPageList(inputObject, outputObject);
    }

}
