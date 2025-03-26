package com.skyeye.school.score.contorller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.service.ScorePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "成分绩管理", tags = "成分绩管理", modelName = "成分绩管理")
public class ScorePartController {

    @Autowired
    private ScorePartService scorePartService;

    @ApiOperation(id = "updateScorePartScore", value = "修改分数信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "score", name = "score", value = "成绩", required = "required")})
    @RequestMapping("/post/ScorePartController/updateScorePartScore")
    public void updateScorePartScore(InputObject inputObject, OutputObject outputObject) {
        scorePartService.updateScorePart(inputObject, outputObject);
    }

    @ApiOperation(id = "createScorePart", value = "自定义新增任务信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScorePart.class)
    @RequestMapping("/post/ScorePartController/createScorePart")
    public void createScorePart(InputObject inputObject, OutputObject outputObject) {
        scorePartService.createScorePart(inputObject, outputObject);
    }

    @ApiOperation(id = "updateScorePartProportion", value = "修改占比信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "workId", name = "workId", value = "任务id", required = "required"),
        @ApiImplicitParam(id = "proportion", name = "proportion", value = "占比", required = "required")})
    @RequestMapping("/post/ScorePartController/updateScorePartProportion")
    public void updateScorePartProportion(InputObject inputObject, OutputObject outputObject) {
        scorePartService.updateScorePartProportion(inputObject, outputObject);
    }
}
