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
 * @ClassName: PatrolPlanPoint
 * @Description: 巡检计划点位关联实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "crm_service_patrol_plan_point")
@ApiModel("巡检计划点位关联实体类")
public class PatrolPlanPoint extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "plan_id")
    @ApiModelProperty(value = "计划ID", required = "required")
    private String planId;

    @TableField(value = "point_id")
    @ApiModelProperty(value = "点位ID", required = "required")
    private String pointId;

    @TableField(exist = false)
    @ApiModelProperty(value = "点位信息")
    private Map<String, Object> pointMation;

}

