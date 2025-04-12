package com.skyeye.eve.chen.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwAnChenRadio;
import com.skyeye.eve.chen.service.DwAnChenRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 矩阵单选题", tags = "答卷 矩阵单选题", modelName = "答卷 矩阵单选题")
public class DwAnChenRadioController {

    @Autowired
    private DwAnChenRadioService dwAnChenRadioService;

    /**
     * 新增/编辑矩阵单选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnChenRadio", value = "新增/编辑矩阵单选题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnChenRadio.class)
    @RequestMapping("/post/DwAnChenRadioController/writeDwAnChenRadio")
    public void writeDwAnChenRadio(InputObject inputObject, OutputObject outputObject) {
        dwAnChenRadioService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵单选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenRadioList", value = "获取矩阵单选题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnChenRadioController/queryDwAnChenRadioList")
    public void queryDwAnChenRadioList(InputObject inputObject, OutputObject outputObject) {
        dwAnChenRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵单选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnChenRadioById", value = "删除矩阵单选题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenRadioController/deleteDwAnChenRadioById")
    public void deleteDwAnChenRadioById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵单选题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenRadioListById", value = "根据id获取矩阵单选题列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenRadioController/queryDwAnChenRadioListById")
    public void queryDwAnChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenRadioService.queryDwAnChenRadioListById(inputObject, outputObject);
    }

}
