/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.controller;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.service.MySchoolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MySchoolTaskController {

    @Autowired
    private MySchoolTaskService mySchoolTaskService;

    /**
     * 获取我的待阅卷列表
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/MySchoolTaskController/queryMyWaitMarkingList")
    public void queryMyWaitMarkingList(InputObject inputObject, OutputObject outputObject) {
        mySchoolTaskService.queryMyWaitMarkingList(inputObject, outputObject);
    }

    /**
     * 获取我的已阅卷列表
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/MySchoolTaskController/queryMyEndMarkingList")
    public void queryMyEndMarkingList(InputObject inputObject, OutputObject outputObject) {
        mySchoolTaskService.queryMyEndMarkingList(inputObject, outputObject);
    }

}
