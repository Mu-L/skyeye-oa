package com.skyeye.school.score.contorller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreTypeChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "成绩类型子表管理", tags = "成绩类型子表管理", modelName = "成绩类型子表管理")
public class ScoreTypeChildController {

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @ApiOperation(id = "writeScoreTypeChild", value = "新增/编辑成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScoreTypeChild.class)
    @RequestMapping("/post/ScoreTypeChildController/writeScoreTypeChild")
    public void writeScoreTypeChild(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "selectScoreTypeById", value = "根据id查询成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/selectScoreTypeById")
    public void selectScoreTypeById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteScoreTypeChildById", value = "根据id删除成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/deleteScoreTypeChildById")
    public void deleteScoreTypeChildById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryListByType", value = "根据类型查询科目和班级的成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "classId", name = "classId", value = "班级id", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/queryListByType")
    public void queryListByType(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "boundDataOrNot", value = "关联或取消关联成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父级id(平时成绩的主键id)"),
        @ApiImplicitParam(id = "id", name = "id", value = "子数据id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/boundDataOrNot")
    public void boundDataOrNOt(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.boundDataOrNot(inputObject, outputObject);
    }

    @ApiOperation(id = "changeProportion", value = "修改成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "proportion", name = "proportion", value = "占比(80即占比80%)",required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "子数据id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/changeProportion")
    public void changeProportion(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.changeProportion(inputObject, outputObject);
    }
}
