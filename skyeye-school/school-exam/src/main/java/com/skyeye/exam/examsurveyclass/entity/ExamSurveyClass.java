package com.skyeye.exam.examSurveyClass.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

/**
 * @ClassName: ExamSurveyClass
 * @Description: 试卷与班级关系表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "Exam:class")
@TableName(value = "exam_survey_class")
@ApiModel("试卷与班级关系表实体类")
public class ExamSurveyClass extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("class_id")
    @ApiModelProperty(value = "班级id", required = "required")
    private String classId;

    @TableField("exam_survey_id")
    @ApiModelProperty(value = "试卷id", required = "required")
    private String examSurveyId;
}