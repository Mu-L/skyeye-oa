package com.skyeye.school.score.contorller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.service.ScorePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "成分绩管理", tags = "成分绩管理", modelName = "成分绩管理")
public class ScorePartController {

    @Autowired
    private ScorePartService scorePartService;

    @ApiOperation(id = "updateScorePart", value = "新增成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "score", name = "score", value = "成绩", required = "required")})
    @RequestMapping("/post/ScorePartController/updateScorePart")
    public void updateScorePart(InputObject inputObject, OutputObject outputObject) {
        scorePartService.updateScorePart(inputObject, outputObject);
    }
}
