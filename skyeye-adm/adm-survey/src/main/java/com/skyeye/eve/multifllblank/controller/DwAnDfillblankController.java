package com.skyeye.eve.multifllblank.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;
import com.skyeye.eve.multifllblank.service.DwAnDfillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 多行填空题保存表", tags = "答卷 多行填空题保存表", modelName = "答卷 多行填空题保存表")
public class DwAnDfillblankController {

    @Autowired
    private DwAnDfillblankService dwAnDfilllankService;

    /**
     * 新增/编辑多行填空题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnDfillblank", value = "新增/编辑多行填空题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnDfillblank.class)
    @RequestMapping("/post/DwAnDfillblankController/writeDwAnDfillblank")
    public void writeDwAnDfillblank(InputObject inputObject, OutputObject outputObject) {
        dwAnDfilllankService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取多行填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnDfillblankList", value = "获取多行填空题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnDfillblankController/queryDwAnDfillblankList")
    public void queryDwAnDfillblankList(InputObject inputObject, OutputObject outputObject) {
        dwAnDfilllankService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除多行填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnDfillblankById", value = "删除多行填空题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnDfillblankController/deleteDwAnDfillblankById")
    public void deleteDwAnDfillblankById(InputObject inputObject, OutputObject outputObject) {
        dwAnDfilllankService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取多行填空题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnDfillblankById", value = "根据id获取多行填空题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnDfillblankController/queryDwAnDfillblankById")
    public void queryDwAnDfillblankById(InputObject inputObject, OutputObject outputObject) {
        dwAnDfilllankService.queryDwAnDfillblankById(inputObject, outputObject);
    }
}
