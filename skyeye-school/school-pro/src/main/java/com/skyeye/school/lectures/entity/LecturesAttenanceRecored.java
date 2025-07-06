package com.skyeye.school.lectures.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.entity.features.Version;
import lombok.Data;
import org.nutz.dao.impl.sql.ValueEscaper;

import java.util.List;

@Data
@TableName(value = "school_lectures_attenance_recored")
@ApiModel(value = "质评-听课记录表实体类")
public class LecturesAttenanceRecored extends Version {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("review_model_id")
    @ApiModelProperty("听评表模型id")
    private String reviewModelId;

    @TableField("course_id")
    @ApiModelProperty("课程id")
    private String courseId;

    @TableField("score_name")
    @ApiModelProperty("课程名称")
    private String scoreName;

    @TableField("teacher_id")
    @ApiModelProperty("任课教师id")
    private String teacherId;

    @TableField("class_id")
    @ApiModelProperty("授课班级id")
    private String classId;

    @TableField("class_name")
    @ApiModelProperty("班级名字(不可修改)")
    private String className;

    @TableField("major_id")
    @ApiModelProperty("专业id")
    private String majorId;

    @TableField("major_name")
    @ApiModelProperty("专业名字(不可修改)")
    private String majorName;

    @TableField("college_id")
    @ApiModelProperty("学院id")
    private String collegeId;

    @TableField("college_name")
    @ApiModelProperty("学院名字")
    private String collegeName;

    @TableField("department_id")
    @ApiModelProperty("教师所属部门id")
    private String departmentId;

    @TableField("department_name")
    @ApiModelProperty("部门名字(不可修改)")
    private String departmentName;

    @TableField("should_num")
    @ApiModelProperty("应到人数")
    private Integer shouldNum;

    @TableField("actual_num")
    @ApiModelProperty("实到人数")
    private Integer actualNum;

    @TableField("late_num")
    @ApiModelProperty("迟到人数")
    private Integer lateNum;

    @TableField("leave_early_num")
    @ApiModelProperty("早退人数")
    private Integer leaveEarlyNum;

    @TableField("teach_time")
    @ApiModelProperty("授课时间(yyyy-mm-dd)")
    private String teachTime;

    @TableField("teach_week")
    @ApiModelProperty("授课时间(周)")
    private String teachWeek;

    @TableField("teach_what_week")
    @ApiModelProperty("授课时间(星期几)")
    private String teachWhatWeek;

    @TableField("teach_section")
    @ApiModelProperty("授课时间(第几节)")
    private String teachSection;

    @TableField("place_id")
    @ApiModelProperty("授课地点")
    private String placeId;

    @TableField("context")
    @ApiModelProperty("授课内容")
    private String context;

    @TableField("class_study_style_grade")
    @ApiModelProperty("授课班级学风情况评价(1优,2良,3中等,4较差)")
    private Integer classStudyStyle;

    @TableField("evaluation_teach_style")
    @ApiModelProperty("对教风的评价、意见或建议")
    private String evaluationTeachStyle;

    @TableField("attend_lecture_teacher_id")
    @ApiModelProperty("听课教师id(该字段有值则表示该教师已签名)")
    private String attendLectureTeacherId;

    @TableField(exist = false) // 非数据库字段
    @ApiModelProperty("听课教师完整信息")
    private String attendLectureTeacherMation; // 类型根据实际需要调整

    @TableField(exist = false)
    @ApiModelProperty("听课记录表管理")
    private List<LecturesAttenanceRecoredChild> LecturesAttenanceRecoredChildList;

}
