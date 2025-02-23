package com.skyeye.eve.checkbox.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "多选题答卷管理", tags = "多选题答卷管理", modelName = "多选题答卷管理")
public class DwAuCheckboxController {

    @Autowired
    private DwAnCheckboxService dwAnCheckboxService;

    /**
     * 新增/编辑多选题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnCheckbox", value = "新增/编辑多选题保存表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DwAnCheckbox.class)
    @RequestMapping("/post/DwAuCheckboxController/writeDwAnCheckbox")
    public void writeDwAnCheckbox(InputObject inputObject, OutputObject outputObject) {
        dwAnCheckboxService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnCheckboxList", value = "获取多选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAuCheckboxController/queryDwAnCheckboxList")
    public void queryDwAnCheckboxList(InputObject inputObject, OutputObject outputObject) {
        dwAnCheckboxService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnCheckboxById", value = "删除多选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAuCheckboxController/deleteDwAnCheckboxById")
    public void deleteDwAnCheckboxById(InputObject inputObject, OutputObject outputObject) {
        dwAnCheckboxService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取多选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnCheckboxListById", value = "根据id获取多选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAuCheckboxController/queryDwAnCheckboxListById")
    public void queryDwAnCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        dwAnCheckboxService.queryDwAnCheckboxListById(inputObject, outputObject);
    }
}
