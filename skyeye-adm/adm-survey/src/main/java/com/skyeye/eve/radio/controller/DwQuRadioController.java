package com.skyeye.eve.radio.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.radio.service.DwQuRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "单选题选项表管理", tags = "单选题选项表管理", modelName = "单选题选项表管理")
public class DwQuRadioController {
    @Autowired
    private DwQuRadioService dwQuRadioService;

    /**
     * 分页获取单选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuRadioList", value = "分页获取单选题选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuRadioController/queryDwQuRadioList")
    public void queryDwQuRadioList(InputObject inputObject, OutputObject outputObject) {
        dwQuRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除单选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuRadioById", value = "根据ID物理删除单选题选项表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuRadioController/deleteDwQuRadioById")
    public void deleteDwQuRadioById(InputObject inputObject, OutputObject outputObject) {
        dwQuRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除单选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除单选题选项表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuRadioController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        dwQuRadioService.changeVisibility(inputObject, outputObject);
    }

}
