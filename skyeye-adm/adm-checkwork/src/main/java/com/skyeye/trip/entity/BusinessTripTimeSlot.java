/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.worktime.entity.CheckWorkTime;
import lombok.Data;

/**
 * @ClassName: BusinessTripTimeSlot
 * @Description: 出差申请出差时间段实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "check_work_business_travel_time_slot", autoResultMap = true)
@ApiModel("出差申请出差时间段实体类")
public class BusinessTripTimeSlot extends SkyeyeLinkData {

    @TableField(value = "time_id")
    @ApiModelProperty(value = "班次id", required = "required")
    private String timeId;

    @TableField(exist = false)
    @Property(value = "班次信息")
    private CheckWorkTime timeMation;

    @TableField(value = "travel_day")
    @ApiModelProperty(value = "出差日期，格式：yyyy-MM-dd", required = "required")
    private String travelDay;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "出差开始时间，格式：HH:mm:ss", required = "required")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "出差结束时间，格式：HH:mm:ss", required = "required")
    private String endTime;

    @TableField(value = "travel_hour")
    @ApiModelProperty(value = "出差小时", required = "required")
    private String travelHour;

}
