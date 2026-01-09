/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ordertype.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealOrderType
 * @Description: 工单类型实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@RedisCacheField(name = "seal:server:orderType")
@TableName(value = "crm_service_order_type")
@ApiModel("工单类型实体类")
public class SealOrderType extends BaseGeneralInfo {

    @TableField(value = "code_number")
    @ApiModelProperty(value = "类型编码", fuzzyLike = true)
    private String codeNumber;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "工单提交开始时间")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "工单提交结束时间")
    private String endTime;

    @TableField(value = "is_allow_all_staff")
    @ApiModelProperty(value = "是否允许所有人接单", enumClass = WhetherEnum.class, required = "num")
    private Integer isAllowAllStaff;

    @TableField(exist = false)
    @ApiModelProperty(value = "允许接单的人员ID列表")
    private List<String> allowedStaffId;

    @TableField(exist = false)
    @Property(value = "执行人")
    private List<Map<String, Object>> allowedStaffMation;

}

