/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.centerrest.entity.checkwork;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: UserStaffHolidayRest
 * @Description: 员工假期信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/26 19:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("员工假期信息实体类")
public class UserStaffHolidayRest implements Serializable {

    @ApiModelProperty(value = "员工id", required = "required")
    private String staffId;

    @ApiModelProperty(value = "年假,精确到6位", required = "required")
    private String quarterYearHour;

    @ApiModelProperty(value = "员工剩余年假数据刷新日期", required = "required")
    private String annualLeaveStatisTime;

    @ApiModelProperty(value = "当前员工剩余补休天数", required = "required")
    private String holidayNumber;

    @ApiModelProperty(value = "员工剩余补休数据刷新日期", required = "required")
    private String holidayStatisTime;

    @ApiModelProperty(value = "当前员工已休补休天数", required = "required")
    private String retiredHolidayNumber;

    @ApiModelProperty(value = "员工已休补休数据刷新日期", required = "required")
    private String retiredHolidayStatisTime;

}
