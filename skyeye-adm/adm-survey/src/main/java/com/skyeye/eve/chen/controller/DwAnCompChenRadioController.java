package com.skyeye.eve.chen.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwAnCompChenRadio;
import com.skyeye.eve.chen.service.DwAnCompChenRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 复合矩阵单选题", tags = "答卷 复合矩阵单选题", modelName = "答卷 复合矩阵单选题")
public class DwAnCompChenRadioController {

    @Autowired
    private DwAnCompChenRadioService dwAnCompChenRadioService;

    /**
     * 新增/编辑复合矩阵单选题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnCompChenRadio", value = "新增/编辑复合矩阵单选题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnCompChenRadio.class)
    @RequestMapping("/post/DwAnCompChenRadioController/writeDwAnCompChenRadio")
    public void writeDwAnCompChenRadio(InputObject inputObject, OutputObject outputObject) {
        dwAnCompChenRadioService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取复合矩阵单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnCompChenRadioList", value = "获取复合矩阵单选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnCompChenRadioController/queryDwAnCompChenRadioList")
    public void queryDwAnCompChenRadioList(InputObject inputObject, OutputObject outputObject) {
        dwAnCompChenRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除复合矩阵单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnCompChenRadioById", value = "删除复合矩阵单选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnCompChenRadioController/deleteDwAnCompChenRadioById")
    public void deleteDwAnCompChenRadioById(InputObject inputObject, OutputObject outputObject) {
        dwAnCompChenRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取复合矩阵单选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnCompChenRadioListById", value = "根据id获取复合矩阵单选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnCompChenRadioController/queryDwAnCompChenRadioListById")
    public void queryDwAnCompChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        dwAnCompChenRadioService.queryDwAnCompChenRadioListById(inputObject, outputObject);
    }
}
