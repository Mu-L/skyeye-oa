package com.skyeye.scheduling.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "check_work_scheduling_time_work", autoResultMap = true)
@ApiModel("排班时间段下工位实体类")
public class SchedulingTimeWork extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "scheduling_time_id")
    @ApiModelProperty(value = "排班时间段id")
    private String schedulingTimeId;

    @TableField(value = "min_staff")
    @ApiModelProperty(value = "最小需求人数")
    private Integer minStaff;

    @TableField(value = "max_staff")
    @ApiModelProperty(value = "最大需求人数")
    private Integer maxStaff;

    @TableField(value = "work_id")
    @ApiModelProperty(value = "工位id")
    private String workId;

    @TableField(value = "scheduling_id")
    @ApiModelProperty(value = "排班id")
    private String schedulingId;

    @TableField(exist = false)
    @ApiModelProperty(value = "排班工位下员工信息")
    private List<SchedulingTimeWorkPeople> schedulingTimeWorkPeopleMation;
}
