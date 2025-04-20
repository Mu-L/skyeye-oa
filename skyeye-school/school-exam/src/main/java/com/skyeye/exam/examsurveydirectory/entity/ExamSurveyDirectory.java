package com.skyeye.exam.examsurveydirectory.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.semester.entity.Semester;
import com.skyeye.school.subject.entity.Subject;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamSurveyDirectory
 * @Description: 试卷实体类
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
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
    @ApiModelProperty(value = "试卷下面有多少题目数")
    private Integer surveyQuNum;

    @TableField("survey_state")
    @ApiModelProperty(value = "试卷状态  0默认设计状态  1执行中 2结束 ", required = "required")
    private Integer surveyState;

    @TableField("real_start_time")
    @ApiModelProperty(value = "实际开始时间")
    private String realStartTime;

    @TableField("real_end_time")
    @ApiModelProperty(value = "实际结束时间")
    private String realEndTime;

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

    @TableField(exist = false)
    @Property(value = "学校信息")
    private School schoolMation;

    @TableField(exist = false)
    @Property(value = "所属院系信息")
    private Faculty facultyMation;

    @TableField(exist = false)
    @Property(value = "专业信息")
    private Major majorMation;

    @TableField("faculty_id")
    @ApiModelProperty(value = "所属学院")
    private String facultyId;

    @TableField("major_id")
    @ApiModelProperty(value = "所属专业")
    private String majorId;

    @TableField("grade_id")
    @ApiModelProperty(value = "所属年级")
    private String gradeId;

    @TableField("class_id")
    @ApiModelProperty(value = "所属班级,多个班级用逗号隔开", required = "required")
    private String classId;

    @TableField("semester_id")
    @ApiModelProperty(value = "学期", required = "required")
    private String semesterId;

    @TableField(exist = false)
    @Property(value = "学期信息")
    private Semester semesterMation;

    @TableField("subject_id")
    @ApiModelProperty(value = "考试科目", required = "required")
    private String subjectId;

    @TableField("fraction")
    @ApiModelProperty(value = "总分数")
    private Integer fraction;

    @TableField("whether_delete")
    @ApiModelProperty(value = "是否删除  1.未删除  2.删除", required = "required")
    private Integer whetherDelete;

    @TableField(exist = false)
    @ApiModelProperty(value = "阅卷人列表", required = "required")
    private String readerList;

    @TableField(exist = false)
    @Property(value = "阅卷人信息列表")
    private List<Map<String, Object>> readerMationList;

    @TableField(exist = false)
    @Property(value = "科目信息")
    private Subject subjectMation;

    @TableField(exist = false)
    @Property(value = "科目信息")
    private List<Subject> subjectListMation;

    @TableField(exist = false)
    @Property(value = "班级信息")
    private List<Classes> classesMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "题目信息", required = "json")
    private List<Question> questionMation;

    @TableField("read_num")
    @ApiModelProperty(value = "已批阅数量")
    private Integer readNum;

    @TableField("unread_num")
    @ApiModelProperty(value = "未批阅数量")
    private Integer unreadNum;

    @TableField("unSubmit_num")
    @ApiModelProperty(value = "未交数量")
    private Integer unSubmitNum;

    @TableField(exist = false)
    @Property(value = "是否回答")
    private Boolean isAnswered;

    @TableField(exist = false)
    @Property(value = "是否是创建人")
    private Boolean isCreated;

    @TableField("is_mark_state")
    @ApiModelProperty(value = "是否批阅试卷（0 待批阅 ，1 已批阅）", defaultValue = "0")
    private Integer isMarkState;

    @TableField("all_number")
    @ApiModelProperty(value = "总人数(一个班或多个班总人数)")
    private Integer allNumber;

}