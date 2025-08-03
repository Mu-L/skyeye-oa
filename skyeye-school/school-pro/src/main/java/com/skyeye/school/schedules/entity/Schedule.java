package com.skyeye.school.schedules.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;

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
public class Schedule extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("school_id")
    @ApiModelProperty("学校id")
    private String schoolId;

    @TableField("faculty_id")
    @ApiModelProperty("院系id")
    private String facultyId;

    @TableField("major_id")
    @ApiModelProperty("专业id")
    private String majorId;

    @TableField("semester_id")
    @ApiModelProperty("学期id")
    private String semesterId;

    @TableField(exist = false)
    @ApiModelProperty(value = "课表额外信息", required = "json")
    private ScheduleChild scheduleChildMation;

    @ApiModelProperty(value = "课程信息", required = "json")
    private List<ScheduleChild> scheduleChildList;
}
