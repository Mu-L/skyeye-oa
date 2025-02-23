package com.skyeye.eve.score.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.score.service.DwAnScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "评分题答卷管理", tags = "评分题答卷管理", modelName = "评分题答卷管理")
public class DwAnScoreController {

    @Autowired
    private DwAnScoreService dwAnScoreService;

    /**
     * 添加或修改评分题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwAnScore", value = "新增/编辑评分题保存表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = DwAnScore.class)
    @RequestMapping("/post/DwAnScoreController/writeDwAnScore")
    public void writeDwAnScore(InputObject inputObject, OutputObject outputObject) {
        dwAnScoreService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取评分题保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnScoreList", value = "获取评分题保存表信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwAnScoreController/queryDwAnScoreList")
    public void queryDwAnScoreList(InputObject inputObject, OutputObject outputObject) {
        dwAnScoreService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除评分题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwAnScoreById", value = "根据ID删除评分题保存表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnScoreController/deleteDwAnScoreById")
    public void deleteDwAnScoreById(InputObject inputObject, OutputObject outputObject) {
        dwAnScoreService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取评分题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwAnScoreListById", value = "根据id获取评分题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwAnScoreController/queryDwAnScoreListById")
    public void queryDwAnScoreListById(InputObject inputObject, OutputObject outputObject) {
        dwAnScoreService.queryDwAnScoreListById(inputObject, outputObject);
    }
}
