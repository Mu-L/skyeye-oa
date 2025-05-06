package com.skyeye.user.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.user.service.UserViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName: UserViewController
 * @Description: 用户访客记录管理
 * @author: skyeye云系列--lqy
 * @date: 2025/5/5 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用户访客记录管理", tags = "用户访客记录管理", modelName = "用户访客记录管理")
public class UserViewController {

    @Autowired
    private UserViewService userViewService;

    @ApiOperation(id = "queryUserVisitors", value = "分页获取用户访客的信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/UserViewController/queryUserVisitors")
    public void queryUserVisitors(InputObject inputObject, OutputObject outputObject) {
        userViewService.queryUserVisitors(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteUserVisitorById", value = "删除访客记录", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
          @ApiImplicitParam(id="id",name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/UserViewController/deleteUserVisitorById")
    public void deleteUserVisitorById(InputObject inputObject, OutputObject outputObject) {
        userViewService.deleteById(inputObject, outputObject);
    }


    @ApiOperation(id = "deleteAllUserVisitors", value = "一键删除访客记录", method = "POST", allUse = "2")
    @RequestMapping("/post/UserViewController/deleteAllUserVisitors")
    public void deleteAllUserVisitors(InputObject inputObject, OutputObject outputObject) {
        userViewService.deleteAllUserVisitors(inputObject, outputObject);
    }

}
