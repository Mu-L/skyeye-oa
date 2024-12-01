package com.skyeye.exam.examSurveyDirectory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: ExamSurveyDirectory
 * @Description: 试卷实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "Exam:directory")
@TableName(value = "exam_survey_directory")
@ApiModel("试卷实体类")
public class ExamSurveyDirectory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("survey_name")
    @ApiModelProperty(value = "试卷名称", required = "required")
    private String surveyName;

    @TableField("survey_note")
    @ApiModelProperty(value = "试卷说明")
    private String surveyNote;

    @TableField("survey_qu_num")
    @ApiModelProperty(value = "试卷下面有多少题目数 ", required = "required")
    private Integer surveyQuNum;

    @TableField("survey_state")
    @ApiModelProperty(value = "试卷状态  0默认设计状态  1执行中 2结束 ", required = "required")
    private Integer surveyState;

    @TableField("real_start_time")
    @ApiModelProperty(value = "实际开始时间")
    private LocalDateTime realStartTime;

    @TableField("real_end_time")
    @ApiModelProperty(value = "实际结束时间")
    private LocalDateTime realEndTime;

    @TableField("survey_model")
    @ApiModelProperty(value = "试卷所属的问卷模块   1.试卷模块  2.作业模块", required = "required")
    private Integer surveyModel;

    @TableField("sid")
    @ApiModelProperty(value = "用于短链接的ID")
    private String sid;

    @TableField("end_type")
    @ApiModelProperty(value = "结束方式   1手动结束,2依据结束时间")
    private Integer endType;

    @TableField("view_answer")
    @ApiModelProperty(value = "是否公开结果  0不  1公开", required = "required")
    private Integer viewAnswer;

    @TableField("school_id")
    @ApiModelProperty(value = "所属学校", required = "required")
    private String schoolId;

    @TableField("grade_id")
    @ApiModelProperty(value = "所属年级")
    private String gradeId;

    @TableField("class_id")
    @ApiModelProperty(value = "所属班级", required = "required")
    private String classId;

    @TableField("semester_id")
    @ApiModelProperty(value = "学期", required = "required")
    private String semesterId;

    @TableField("subject_id")
    @ApiModelProperty(value = "考试科目", required = "required")
    private String subjectId;

    @TableField("session_year")
    @ApiModelProperty(value = "哪一届的学生，比如：2013")
    private String sessionYear;

    @TableField("fraction")
    @ApiModelProperty(value = "总分数")
    private Integer fraction;

    @TableField("whether_delete")
    @ApiModelProperty(value = "是否删除  1.未删除  2.删除", required = "required")
    private Integer whetherDelete;
}