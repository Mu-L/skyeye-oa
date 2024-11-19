package com.skyeye.school.test.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.test.entity.Test;
import com.skyeye.school.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "测试案例",tags = "测试案例",modelName = "测试案例")
public class TestController {

    @Autowired
    private TestService testService;

    // 新增接口
    @ApiOperation(id ="writeTest", value ="新增/编辑",method ="POST", allUse ="2")
    @ApiImplicitParams(classBean = Test.class)
    @RequestMapping("/post/TestController/writeTest")
    public void writetest(InputObject inputObject, OutputObject outputObject) {
        testService.saveOrUpdateEntity(inputObject, outputObject);
    }

    //  根据id查询接口
    @ApiOperation(id ="selectTestByID", value ="根据id查询用户",method ="GET", allUse ="2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id",name = "id",value = "主键id",required = "required")})
    @RequestMapping("/post/TestController/selectTestByID")
    public void selectTest(InputObject inputObject, OutputObject outputObject) {
        testService.selectById(inputObject, outputObject);
    }


    //  分页查询
    @ApiOperation(id ="selectTestList", value ="获取用户列表",method ="POST", allUse ="2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TestController/selectTestList")
    public void selectTestList(InputObject inputObject, OutputObject outputObject) {
        testService.queryPageList(inputObject, outputObject);
    }


    //  查询所有用户
        @ApiOperation(id ="selectTestAll", value ="获取所有用户列表",method ="POST", allUse ="2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TestController/selectTestAll")
    public void selectTestAll(InputObject inputObject, OutputObject outputObject) {
        testService.queryAlltestall(inputObject, outputObject);
    }



        //  根据id删除用户
        @ApiOperation(id ="deleteTestById", value ="根据id删除用户",method ="DELETE", allUse ="2")
        @ApiImplicitParams({
                @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
        @RequestMapping("/post/TestController/deleteTestById")
        public void deleteTestById(InputObject inputObject, OutputObject outputObject) {
            testService.deleteById(inputObject, outputObject);
        }


}
