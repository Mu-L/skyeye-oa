package com.skyeye.school.schedules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;


/**
 * @ClassName: Schedule
 * @Description: 排课表子表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */

@Data
@TableName(value = "school_lectures_schedules_child", autoResultMap = true)
@ApiModel(value = "排课表子表实体类")
public class ScheduleChild extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty(value = "父表id,排课表id")
    private String parentId;

    @TableField("course_id")
    @ApiModelProperty(value = "课程id", required = "required")
    private String courseId;

    @TableField(exist = false)
    @Property(value = "课程名称")
    private Map<String,Object> courseMation;

    @TableField("teacher_id")
    @ApiModelProperty(value = "教师id", required = "required")
    private String teacherId;

    @TableField(exist = false)
    @Property(value = "教师信息")
    private Map<String, Object> teacherMation;

    @TableField("classroom_id")
    @ApiModelProperty(value = "教室id", required = "required")
    private String classroomId;

    @TableField(exist = false)
    @Property("教室信息")
    private Map<String, Object> classroomMation;

    @TableField("week_day")
    @ApiModelProperty(value = "星期几 2、3、4、5、6、7、1", required = "required,num")
    private Integer weekDay;

    @TableField("start_time")
    @ApiModelProperty(value = "开始时间", required = "required")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty(value = "结束时间", required = "required")
    private String endTime;

    @TableField("start_week")
    @ApiModelProperty(value = "开始周", required = "required,num")
    private Integer startWeek;

    @TableField("end_week")
    @ApiModelProperty(value = "结束周", required = "required,num")
    private Integer endWeek;

    @TableField("start_num")
    @ApiModelProperty(value = "开始节数", required = "required,num")
    private Integer startNum;

    @TableField("end_num")
    @ApiModelProperty(value = "结束节数", required = "required,num")
    private Integer endNum;

    @TableField("credits")
    @ApiModelProperty(value = "学分", required = "required,num")
    private Integer credits;

    @TableField("student_hour")
    @ApiModelProperty(value = "学时", required = "required,num")
    private Integer studentHour;

}
