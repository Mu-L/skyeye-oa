/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.interviewee.entity.Interviewee;
import com.skyeye.interviewee.service.IntervieweeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IntervieweeController
 * @Description: 面试者管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/11/27 13:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "面试者管理", tags = "面试者管理", modelName = "面试者管理")
public class IntervieweeController {

    @Autowired
    private IntervieweeService intervieweeService;

    /**
     * 获取面试者信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "bossInterviewee001", value = "获取面试者信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/IntervieweeController/queryBossIntervieweeList")
    public void queryBossIntervieweeList(InputObject inputObject, OutputObject outputObject) {
        intervieweeService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑面试者信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeInterviewee", value = "新增/编辑面试者信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Interviewee.class)
    @RequestMapping("/post/IntervieweeController/writeInterviewee")
    public void insertBossInterviewee(InputObject inputObject, OutputObject outputObject) {
        intervieweeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除面试者信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteIntervieweeById", value = "删除待面试者", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/IntervieweeController/deleteIntervieweeById")
    public void deleteIntervieweeById(InputObject inputObject, OutputObject outputObject) {
        intervieweeService.deleteById(inputObject, outputObject);
    }

}
