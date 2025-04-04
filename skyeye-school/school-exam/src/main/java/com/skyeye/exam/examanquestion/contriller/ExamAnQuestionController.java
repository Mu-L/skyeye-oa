package com.skyeye.exam.examanquestion.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanquestion.entity.ExamAnQuestion;
import com.skyeye.exam.examanquestion.service.ExamAnQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnQuestionController
 * @Description: 答卷 试题答案相关信息表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 试题答案相关信息表", tags = "答卷 试题答案相关信息表", modelName = "答卷 试题答案相关信息表")
public class ExamAnQuestionController {

    @Autowired
    private ExamAnQuestionService examAnQuestionService;

    /**
     * 新增/编辑试题答案相关信息表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnQuestion", value = "新增/编辑试题答案相关信息表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnQuestion.class)
    @RequestMapping("/post/ExamAnQuestionController/writeExamAnQuestion")
    public void writeExamAnQuestion(InputObject inputObject, OutputObject outputObject) {
        examAnQuestionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取试题答案相关信息表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnQuestionList", value = "获取试题答案相关信息表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnQuestionController/queryExamAnQuestionList")
    public void queryExamAnQuestionList(InputObject inputObject, OutputObject outputObject) {
        examAnQuestionService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除试题答案相关信息表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnQuestionById", value = "删除试题答案相关信息表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnQuestionController/deleteExamAnQuestionById")
    public void deleteExamAnQuestionById(InputObject inputObject, OutputObject outputObject) {
        examAnQuestionService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id试题答案相关信息表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnQuestionById", value = "根据id试题答案相关信息表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnQuestionController/queryExamAnQuestionById")
    public void queryExamAnQuestionById(InputObject inputObject, OutputObject outputObject) {
        examAnQuestionService.queryExamAnQuestionById(inputObject, outputObject);
    }

}
