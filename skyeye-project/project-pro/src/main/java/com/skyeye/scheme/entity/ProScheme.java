/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.Version;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProScheme
 * @Description: 项目方案实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "pm:projectScheme", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "pro_scheme")
@ApiModel("项目方案实体类")
public class ProScheme extends Version {

    @TableField("`name`")
    @ApiModelProperty(value = "名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "scheme_code", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "方案编号", fuzzyLike = true)
    private String schemeCode;

    @TableField(value = "project_background")
    @ApiModelProperty(value = "项目背景")
    private String projectBackground;

    @TableField(value = "project_objective")
    @ApiModelProperty(value = "项目目标")
    private String projectObjective;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "预计开始时间")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "预计结束时间")
    private String endTime;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目id", required = "required")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private Object projectMation;

    @TableField(value = "budget")
    @ApiModelProperty(value = "项目总预算", required = "required,double", defaultValue = "0")
    private String budget;

    @TableField(value = "budget_explanation")
    @ApiModelProperty(value = "预算说明")
    private String budgetExplanation;

    @TableField(value = "tech_route")
    @ApiModelProperty(value = "技术路线")
    private String techRoute;

    @TableField(value = "architecture_design")
    @ApiModelProperty(value = "架构设计")
    private String architectureDesign;

    @TableField(value = "implementation_plan")
    @ApiModelProperty(value = "实施方案")
    private String implementationPlan;

    @TableField(value = "tech_difficulties")
    @ApiModelProperty(value = "技术难点与解决方案")
    private String techDifficulties;

    @TableField(value = "scheme_content")
    @ApiModelProperty(value = "方案描述")
    private String schemeContent;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "客户/供应商id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "客户/供应商业务数据的key", required = "required")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "客户/供应商信息")
    private Map<String, Object> objectMation;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "预算明细列表", required = "required,json")
    private List<ProSchemeBudgetDetail> budgetDetailList;

}

