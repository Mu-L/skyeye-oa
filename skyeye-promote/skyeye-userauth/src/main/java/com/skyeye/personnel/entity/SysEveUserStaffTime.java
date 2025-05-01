/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: SysEveUserStaffTime
 * @Description: 员工绑定的考勤班次实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/12 22:13
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "sys_eve_user_staff_time", autoResultMap = true)
@ApiModel("员工绑定的考勤班次实体类")
public class SysEveUserStaffTime extends CommonInfo {

    @TableField(value = "staff_id")
    @ApiModelProperty(value = "员工ID", required = "required")
    private String staffId;

    @TableField(exist = false)
    @Property(value = "员工信息")
    private Map<String, Object> staffMation;

    @TableField(value = "check_work_time_id")
    @ApiModelProperty(value = "班次id", required = "required")
    private String checkWorkTimeId;

    @TableField(exist = false)
    @Property(value = "班次信息")
    private Map<String, Object> checkWorkTimeMation;

}
