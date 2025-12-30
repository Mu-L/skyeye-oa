/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.leave.classenum.UseYearHolidayType;
import com.skyeye.worktime.entity.CheckWorkTime;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: LeaveTimeSlot
 * @Description: 请假申请请假时间段实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "check_work_leave_time_slot", autoResultMap = true)
@ApiModel("请假申请请假时间段实体类")
public class LeaveTimeSlot extends SkyeyeLinkData {

    @TableField(value = "time_id")
    @ApiModelProperty(value = "班次id", required = "required")
    private String timeId;

    @TableField(exist = false)
    @Property(value = "班次信息")
    private CheckWorkTime timeMation;

    @TableField(value = "leave_type")
    @ApiModelProperty(value = "请假类型", required = "required")
    private String leaveType;

    @TableField(exist = false)
    @Property(value = "请假类型信息")
    private Map<String, Object> leaveTypeMation;

    @TableField(value = "leave_day")
    @ApiModelProperty(value = "请假日期，格式：yyyy-MM-dd", required = "required")
    private String leaveDay;

    @TableField(value = "leave_start_time")
    @ApiModelProperty(value = "请假开始时间，格式：HH:mm:ss", required = "required")
    private String leaveStartTime;

    @TableField(value = "leave_end_time")
    @ApiModelProperty(value = "请假结束时间，格式：HH:mm:ss", required = "required")
    private String leaveEndTime;

    @TableField(value = "leave_hour")
    @ApiModelProperty(value = "请假工时", required = "required")
    private String leaveHour;

    @TableField(value = "use_year_holiday")
    @Property(value = "是否使用年假/补休", enumClass = UseYearHolidayType.class)
    private Integer useYearHoliday;

    @TableField(exist = false)
    @Property(value = "是否使用年假/补休的显示名称")
    private String useYearHolidayName;

}
