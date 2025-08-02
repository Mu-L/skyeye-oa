package com.skyeye.school.schedules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;


/**
 * @ClassName: Schedule
 * @Description: 排课表子表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */

@Data
@TableName(value = "school_lectures_schedules_child")
@ApiModel(value = "排课表子表实体类")
public class ScheduleChild extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty("父表id,排课表id")
    private String parentId;

    @TableField("course_id")
    @ApiModelProperty("课程id")
    private String courseId;

    @TableField("teacher_id")
    @ApiModelProperty("教师id")
    private String teacherId;

    @TableField("classroom_id")
    @ApiModelProperty("教室id")
    private String classroomId;

    @TableField("week_day")
    @ApiModelProperty("星期几 2、3、4、5、6、7、1")
    private Integer weekDay;

    @TableField("start_time")
    @ApiModelProperty("开始时间")
    private String startTime;

    @TableField("end_time")
    @ApiModelProperty("结束时间")
    private String endTime;

    @TableField("start_week")
    @ApiModelProperty("开始周")
    private Integer startWeek;

    @TableField("end_week")
    @ApiModelProperty("结束周")
    private Integer endWeek;

    @TableField("start_num")
    @ApiModelProperty("开始节数")
    private Integer startNum;

    @TableField("end_num")
    @ApiModelProperty("结束节数")
    private Integer endNum;

    @TableField("credits")
    @ApiModelProperty("学分")
    private Integer credits;

    @TableField("student_hour")
    @ApiModelProperty("学时")
    private Integer studentHour;

    @TableField("class_id")
    @ApiModelProperty("班级id")
    private String classId;
}
