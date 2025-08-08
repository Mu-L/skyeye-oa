package com.skyeye.school.schedules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: Schedule
 * @Description: 排课表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */

@Data
@TableName(value = "school_lectures_schedules")
@ApiModel(value = "排课表实体类")
public class Schedule extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("school_id")
    @ApiModelProperty(value = "学校id", required = "required")
    private String schoolId;

    @TableField(exist = false)
    @Property(value = "学校信息")
    private Map<String, Object> schoolMation;

    @TableField("faculty_id")
    @ApiModelProperty(value = "院系id", required = "required")
    private String facultyId;

    @TableField(exist = false)
    @Property(value = "院系信息")
    private Map<String, Object> facultyMation;

    @TableField("major_id")
    @ApiModelProperty(value = "专业id", required = "required")
    private String majorId;

    @TableField(exist = false)
    @Property(value = "专业信息")
    private Map<String, Object> majorMation;

    @TableField("semester_id")
    @ApiModelProperty(value = "学期id", required = "required")
    private String semesterId;

    @TableField(exist = false)
    @Property(value = "学期信息")
    private Map<String, Object> semesterMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "课程信息", required = "required,json")
    private List<ScheduleChild> scheduleChildList;

    @TableField("class_id")
    @ApiModelProperty(value = "班级id", required = "required")
    private String classId;

    @TableField(exist = false)
    @Property("班级信息")
    private Map<String, Object> classMation;
}
