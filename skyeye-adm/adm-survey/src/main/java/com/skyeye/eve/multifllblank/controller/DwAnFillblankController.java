package com.skyeye.eve.multifllblank.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwAnFillblank;
import com.skyeye.eve.multifllblank.service.DwAnFillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 填空题保存表", tags = "答卷 填空题保存表", modelName = "答卷 填空题保存表")
public class DwAnFillblankController {

    @Autowired
    private DwAnFillblankService dwAnFillblankService;

    /**
     * 新增/编辑填空题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnFillblank", value = "新增/编辑填空题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwAnFillblank.class)
    @RequestMapping("/post/DwAnFillblankController/writeDwAnFillblank")
    public void writeDwAnFillblank(InputObject inputObject, OutputObject outputObject) {
        dwAnFillblankService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnFillblankList", value = "获取填空题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnFillblankController/queryDwAnFillblankList")
    public void queryDwAnFillblankList(InputObject inputObject, OutputObject outputObject) {
        dwAnFillblankService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnFillblankById", value = "删除填空题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnFillblankController/deleteDwAnFillblankById")
    public void deleteDwAnFillblankById(InputObject inputObject, OutputObject outputObject) {
        dwAnFillblankService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取填空题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnFillblankListById", value = "根据id获取填空题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnFillblankController/queryDwAnFillblankListById")
    public void queryDwAnFillblankListById(InputObject inputObject, OutputObject outputObject) {
        dwAnFillblankService.queryDwAnFillblankListById(inputObject, outputObject);
    }
}
