/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.jobdiary.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.jobdiary.entity.JobDiary;
import com.skyeye.eve.jobdiary.service.JobDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: JobDiaryController
 * @Description: 工作日志管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/2/24 11:50
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "工作日志", tags = "工作日志", modelName = "工作日志")
public class JobDiaryController {

    @Autowired
    private JobDiaryService jobDiaryService;

    @ApiOperation(id = "diary005", value = "获取我发出日志列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/JobDiaryController/queryMysendJobDiaryList")
    public void queryMysendJobDiaryList(InputObject inputObject, OutputObject outputObject) {
        jobDiaryService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "diary001", value = "获取我收到的日志", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/JobDiaryController/queryMyReceivedJobDiaryList")
    public void queryMyReceivedJobDiaryList(InputObject inputObject, OutputObject outputObject) {
        jobDiaryService.queryMyReceivedJobDiaryList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeJobDiary", value = "新增/编辑日志", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = JobDiary.class)
    @RequestMapping("/post/JobDiaryController/writeJobDiary")
    public void writeJobDiary(InputObject inputObject, OutputObject outputObject) {
        jobDiaryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeJobDiaryById", value = "撤销日志", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JobDiaryController/revokeJobDiaryById")
    public void revokeJobDiaryById(InputObject inputObject, OutputObject outputObject) {
        jobDiaryService.revokeJobDiaryById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteJobDiaryById", value = "删除日志", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JobDiaryController/deleteJobDiaryById")
    public void deleteJobDiaryById(InputObject inputObject, OutputObject outputObject) {
        jobDiaryService.deleteById(inputObject, outputObject);
    }

}
