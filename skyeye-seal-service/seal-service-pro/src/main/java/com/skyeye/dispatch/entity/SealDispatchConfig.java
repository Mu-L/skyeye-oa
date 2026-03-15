/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dispatch.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SealDispatchConfig
 * @Description: 工单派单规则配置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "crm_service_dispatch_config", autoResultMap = true)
@ApiModel("工单派单规则配置实体类")
public class SealDispatchConfig extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "cap_order_quantity")
    @ApiModelProperty(value = "封顶接单量", required = "num")
    private Integer capOrderQuantity;

    @TableField(value = "auto_dispatch_start_time")
    @ApiModelProperty(value = "自动派单开始时间，格式HH:mm")
    private String autoDispatchStartTime;

    @TableField(value = "auto_dispatch_end_time")
    @ApiModelProperty(value = "自动派单结束时间，格式HH:mm")
    private String autoDispatchEndTime;

    @TableField(value = "even_assignment_enabled")
    @ApiModelProperty(value = "系统均匀指派规则是否开启", enumClass = WhetherEnum.class, required = "num")
    private Integer evenAssignmentEnabled;

    @TableField(value = "pool_cap_quantity")
    @ApiModelProperty(value = "工单池封顶接单量", required = "num")
    private Integer poolCapQuantity;

    @TableField(value = "pool_count_suspended")
    @ApiModelProperty(value = "是否计算工单池暂停工单", enumClass = WhetherEnum.class, required = "num")
    private Integer poolCountSuspended;

    @TableField(value = "system_rules", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "系统派单规则列表，JSON格式", required = "required,json")
    private List<Map<String, Object>> systemRules;

}
