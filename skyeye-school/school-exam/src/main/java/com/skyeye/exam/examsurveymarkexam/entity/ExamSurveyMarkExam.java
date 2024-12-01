package com.skyeye.exam.examSurveyMarkExam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: ExamSurveyMarkExam
 * @Description: 试卷与阅卷人关系表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "Exam:markexam")
@TableName(value = "exam_survey_mark_exam")
@ApiModel("试卷与阅卷人关系表实体类")
public class ExamSurveyMarkExam extends CommonInfo {

    @TableField("survey_id")
    @ApiModelProperty(value = "试卷id", required = "required")
    private String surveyId;

    @TableField("user_id")
    @ApiModelProperty(value = "阅卷人id（用户id）", required = "required")
    private String userId;
}