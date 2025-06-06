package com.skyeye.eve.yesno.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.yesno.entity.DwAnYesno;
import com.skyeye.eve.yesno.service.DwAnYesnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "判断题保存表管理", tags = "判断题保存表管理", modelName = "判断题保存表管理")
public class DwAnYesnoController {

    @Autowired
    private DwAnYesnoService dwAnYesnoService;

    /**
     * 添加或修改判断题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnYesno", value = "新增/编辑判断题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnYesno.class)
    @RequestMapping("/post/DwAnYesnoController/writeDwAnYesno")
    public void writeDwAnYesno(InputObject inputObject, OutputObject outputObject) {
        dwAnYesnoService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取判断题保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnYesnoList", value = "获取判断题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnYesnoController/queryDwAnYesnoList")
    public void queryDwAnYesnoList(InputObject inputObject, OutputObject outputObject) {
        dwAnYesnoService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除判断题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnYesnoById", value = "根据ID删除单选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnYesnoController/deleteDwAnYesnoById")
    public void deleteDwAnYesnoById(InputObject inputObject, OutputObject outputObject) {
        dwAnYesnoService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取判断题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnYesnoListById", value = "根据id获取单选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnYesnoController/queryDwAnYesnoListById")
    public void queryDwAnYesnoListById(InputObject inputObject, OutputObject outputObject) {
        dwAnYesnoService.queryDwAnYesnoListById(inputObject, outputObject);
    }

}
