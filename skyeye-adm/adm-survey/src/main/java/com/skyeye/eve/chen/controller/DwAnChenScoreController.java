package com.skyeye.eve.chen.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.chen.entity.DwAnChenScore;
import com.skyeye.eve.chen.service.DwAnChenScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "答卷 矩阵多选题", tags = "答卷 矩阵多选题", modelName = "答卷 矩阵多选题")
public class DwAnChenScoreController {

    @Autowired
    private DwAnChenScoreService dwAnChenScoreService;

    /**
     * 新增/编辑矩阵多选题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnChenScore", value = "新增/编辑矩阵多选题保存表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DwAnChenScore.class)
    @RequestMapping("/post/DwAnChenScoreController/writeDwAnChenScore")
    public void writeDwAnChenScore(InputObject inputObject, OutputObject outputObject) {
        dwAnChenScoreService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenScoreList", value = "获取矩阵多选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnChenScoreController/queryDwAnChenScoreList")
    public void queryDwAnChenScoreList(InputObject inputObject, OutputObject outputObject) {
        dwAnChenScoreService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnChenScoreById", value = "删除矩阵多选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenScoreController/deleteDwAnChenScoreById")
    public void deleteDwAnChenScoreById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenScoreService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵多选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnChenScoreListById", value = "根据id获取矩阵多选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnChenScoreController/queryDwAnChenScoreListById")
    public void queryDwAnChenScoreListById(InputObject inputObject, OutputObject outputObject) {
        dwAnChenScoreService.queryDwAnChenScoreListById(inputObject, outputObject);
    }

}
