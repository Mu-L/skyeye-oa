/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.job.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.job.entity.JobResume;
import com.skyeye.job.service.JobResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: JobResumeController
 * @Description: 员工工作履历管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/17 7:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工工作履历信息", tags = "员工工作履历信息", modelName = "员工工作履历信息")
public class JobResumeController {

    @Autowired
    private JobResumeService jobResumeService;

    @ApiOperation(id = "queryJobResumeList", value = "查询工作履历列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/JobResumeController/queryJobResumeList")
    public void queryJobResumeList(InputObject inputObject, OutputObject outputObject) {
        jobResumeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeJobResume", value = "新增/编辑员工工作履历信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = JobResume.class)
    @RequestMapping("/post/JobResumeController/writeJobResume")
    public void writeJobResume(InputObject inputObject, OutputObject outputObject) {
        jobResumeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteJobResumeById", value = "根据id删除员工工作履历信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/JobResumeController/deleteJobResumeById")
    public void deleteJobResumeById(InputObject inputObject, OutputObject outputObject) {
        jobResumeService.deleteById(inputObject, outputObject);
    }

}
