/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cancleleave.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import com.skyeye.worktime.entity.CheckWorkTime;
import lombok.Data;

/**
 * @ClassName: CancelLeaveTimeSlot
 * @Description: 销假申请销假时间段实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 15:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "check_work_cancel_leave_time_slot", autoResultMap = true)
@ApiModel("销假申请销假时间段实体类")
public class CancelLeaveTimeSlot extends SkyeyeLinkData {

    @TableField(value = "time_id")
    @ApiModelProperty(value = "班次id", required = "required")
    private String timeId;

    @TableField(exist = false)
    @Property(value = "班次信息")
    private CheckWorkTime timeMation;

    @TableField(value = "cancel_day")
    @ApiModelProperty(value = "销假日期，格式：yyyy-MM-dd", required = "required")
    private String cancelDay;

    @TableField(value = "cancel_start_time")
    @ApiModelProperty(value = "销假开始时间，格式：HH:mm:ss", required = "required")
    private String cancelStartTime;

    @TableField(value = "cancel_end_time")
    @ApiModelProperty(value = "销假结束时间，格式：HH:mm:ss", required = "required")
    private String cancelEndTime;

    @TableField(value = "cancel_hour")
    @ApiModelProperty(value = "销假工时", required = "required")
    private String cancelHour;

}
