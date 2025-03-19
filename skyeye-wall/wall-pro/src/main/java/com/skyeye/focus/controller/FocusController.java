package com.skyeye.focus.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.focus.entity.Focus;
import com.skyeye.focus.service.FocusService;
import com.skyeye.videotag.entity.VideoTag;
import com.skyeye.videotag.service.VideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FocusController
 * @Description: 视频关注管理
 * @author: skyeye云系列--lqy
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "视频关注管理", tags = "视频关注管理", modelName = "视频关注管理")
public class FocusController {
    @Autowired
    private FocusService focusService;


    /**
     * 新增视频关注
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    // TODO 只要新增
    @ApiOperation(id = "insertFocus", value = "新增视频关注", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Focus.class)
    @RequestMapping("/post/FocusController/insertFocus")
    public void insertFocus(InputObject inputObject, OutputObject outputObject) {
        focusService.createEntity(inputObject, outputObject);
    }

    /**
     * 获取视频关注列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFocusList", value = "获取视频关注列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FocusController/queryFocusList")
    public void queryFocusList(InputObject inputObject, OutputObject outputObject) {
        focusService.queryPageList(inputObject, outputObject);
    }

    /**
     * 取消视频关注
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteFocusById", value = "取消视频关注", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "标签id", required = "required")})
    @RequestMapping("/post/FocusController/deleteFocusById")
    public void deleteFocusById(InputObject inputObject, OutputObject outputObject) {
        focusService.deleteById(inputObject, outputObject);
    }



}
