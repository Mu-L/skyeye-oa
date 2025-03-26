package com.skyeye.school.score.contorller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.score.service.ScoreTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "成绩类型管理", tags = "成绩类型管理", modelName = "成绩类型管理")
public class ScoreTypeController {

    @Autowired
    private ScoreTypeService scoreTypeService;

    @ApiOperation(id = "writeScoreTypeList", value = "新增成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScoreType.class)
    @RequestMapping("/post/ScoreTypeController/writeScoreTypeList")
    public void writeScoreTypeList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "querySameTableDateList", value = "获取同表成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "classId", name = "classId", value = "班级id", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required")})
    @RequestMapping("/post/ScoreTypeController/querySameTableDateList")
    public void querySameTableDateList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.querySameTableDateList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDifferentTableDateList", value = "获取不同表成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "classId", name = "classId", value = "班级id", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required")})
    @RequestMapping("/post/ScoreTypeController/queryDifferentTableDateList")
    public void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.queryDifferentTableDateList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteScoreTypeById", value = "根据id删除成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/deleteScoreTypeById")
    public void deleteScoreTypeById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBySubjectIdAndClassesId", value = "根据科目id和班级id查询成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required"),
        @ApiImplicitParam(id = "classesId", name = "classesId", value = "班级", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/queryBySubjectIdAndClassesId")
    public void queryBySubjectIdAndClassesId(InputObject inputObject, OutputObject outputObject){
        scoreTypeService.queryBySubjectIdAndClassesId(inputObject,outputObject);
    }
}
