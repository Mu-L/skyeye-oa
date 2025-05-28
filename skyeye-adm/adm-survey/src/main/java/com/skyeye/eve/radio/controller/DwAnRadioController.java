package com.skyeye.eve.radio.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.radio.entity.DwAnRadio;
import com.skyeye.eve.radio.service.DwAnRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "单选题答卷表管理", tags = "单选题答卷表管理", modelName = "单选题答卷表管理")
public class DwAnRadioController {

    private final String writeDwAnRadio = "writeDwAnRadio";
    @Autowired
    private DwAnRadioService dwAnRadioService;

    /**
     * 添加单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = writeDwAnRadio, value = "新增单选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnRadio.class)
    @RequestMapping("/post/DwAnRadioController/writeDwAnRadio")
    public void writeDwAnRadio(InputObject inputObject, OutputObject outputObject) {
        dwAnRadioService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取单选题保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnRadioList", value = "获取单选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnRadioController/queryDwAnRadioList")
    public void queryDwAnRadioList(InputObject inputObject, OutputObject outputObject) {
        dwAnRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnRadioById", value = "根据ID删除单选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnRadioController/deleteDwAnRadioById")
    public void deleteDwAnRadioById(InputObject inputObject, OutputObject outputObject) {
        dwAnRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取单选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnRadioListById", value = "根据id获取单选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnRadioController/queryDwAnRadioListById")
    public void queryDwAnRadioListById(InputObject inputObject, OutputObject outputObject) {
        dwAnRadioService.queryDwAnRadioListById(inputObject, outputObject);
    }
}
