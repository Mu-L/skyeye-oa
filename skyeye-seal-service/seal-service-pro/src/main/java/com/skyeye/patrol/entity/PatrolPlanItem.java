/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: PatrolPlanItem
 * @Description: 巡检计划项目关联实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "crm_service_patrol_plan_item")
@ApiModel("巡检计划项目关联实体类")
public class PatrolPlanItem extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "plan_id")
    @ApiModelProperty(value = "计划ID", required = "required")
    private String planId;

    @TableField(value = "item_id")
    @ApiModelProperty(value = "项目ID", required = "required")
    private String itemId;

    @TableField(exist = false)
    @ApiModelProperty(value = "项目信息")
    private Map<String, Object> itemMation;

}

