/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.contorller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ScoreController
 * @Description: 成绩管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/26 12:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "成绩管理", tags = "成绩管理", modelName = "成绩管理")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @ApiOperation(id = "updateScore", value = "修改分数信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required"),
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required"),
        @ApiImplicitParam(id = "scoreList", name = "scoreList", value = "成绩信息列表，格式：[{'id':'成绩id','score':'分数'},...]", required = "required,json")})
    @RequestMapping("/post/ScorePartController/updateScore")
    public void updateScore(InputObject inputObject, OutputObject outputObject) {
        scoreService.updateScore(inputObject, outputObject);
    }

}
