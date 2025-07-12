package com.skyeye.eve.chen.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwAnChenCheckbox;
import com.skyeye.eve.chen.service.DwAnChenCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 矩阵多选题", tags = "答卷 矩阵多选题", modelName = "答卷矩阵多选题")
public class DwAnChenCheckboxController {

    @Autowired
    private DwAnChenCheckboxService dwAnChenCheckboxService;

    /**
     * 新增/编辑矩阵多选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnChenCheckbox", value = "新增/编辑矩阵多选题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnChenCheckbox.class)
    @RequestMapping("/post/DwAnChenCheckboxController/writeDwAnChenCheckbox")

    public void writeDwAnChenCheckbox(InputObject inputObject, OutputObject outputObject) {
        dwAnChenCheckboxService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵多选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenCheckboxList", value = "获取矩阵多选题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnChenCheckboxController/queryDwAnChenCheckboxList")
    public void queryDwAnChenCheckboxList(InputObject inputObject, OutputObject outputObject) {
        dwAnChenCheckboxService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵多选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnChenCheckboxById", value = "删除矩阵多选题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenCheckboxController/deleteDwAnChenCheckboxById")
    public void deleteDwAnChenCheckboxById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenCheckboxService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵多选题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenCheckboxListById", value = "根据id获取矩阵多选题列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenCheckboxController/queryDwAnChenCheckboxListById")
    public void queryDwAnChenCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenCheckboxService.queryDwAnChenCheckboxListById(inputObject, outputObject);
    }

}
