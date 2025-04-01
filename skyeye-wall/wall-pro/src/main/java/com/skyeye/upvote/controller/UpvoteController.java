package com.skyeye.upvote.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.upvote.service.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: UpvoteController
 * @Description: 点赞信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/6 14:31.
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "点赞管理", tags = "点赞管理", modelName = "点赞管理")
public class UpvoteController {

    @Autowired
    private UpvoteService upvoteService;

    @ApiOperation(id = "addOrCancelUpvote", value = "新增/删除点赞信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "第三方业务数据id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "第三方业务数据key", required = "required"),})
    @RequestMapping("/post/UpvoteController/addOrCancelUpvote")
    public void addOrCancelUpvote(InputObject inputObject, OutputObject outputObject) {
        upvoteService.addOrCancelUpvote(inputObject, outputObject);
    }
}