package com.skyeye.exam.examsurveyquanswer.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ExamSurveyQuAnswerBox
 * @Description: 答卷 题目和所得分数的关联表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("答卷 题目和所得分数的关联表实体类")
public class ExamSurveyQuAnswerBox implements Serializable {

    @ApiModelProperty(value = "答卷评分信息", required = "required,json")
    private List<ExamSurveyQuAnswer> examSurveyQuAnswerList;

}