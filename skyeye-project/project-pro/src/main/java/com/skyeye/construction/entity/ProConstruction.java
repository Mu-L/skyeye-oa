package com.skyeye.construction.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.VersionFlowable;
import com.skyeye.project.entity.Project;
import com.skyeye.scheme.entity.ProScheme;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProConstruction
 * @Description: 施工方案实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "pm:projectConstruction", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "pro_construction")
@ApiModel("施工方案实体类")
public class ProConstruction extends VersionFlowable {

    @TableField("`name`")
    @ApiModelProperty(value = "方案名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目id", required = "required")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private Project projectMation;

    @TableField(value = "scheme_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "方案id", required = "required")
    private String schemeId;

    @TableField(exist = false)
    @Property(value = "方案信息")
    private ProScheme schemeMation;

    @TableField(value = "construction_location")
    @ApiModelProperty(value = "施工地点", required = "required")
    private String constructionLocation;

    @TableField(value = "construction_period")
    @ApiModelProperty(value = "施工周期(天)", required = "required,num")
    private String constructionPeriod;

    @TableField(value = "construction_content")
    @ApiModelProperty(value = "施工内容", required = "required")
    private String constructionContent;

    @TableField(value = "construction_plan")
    @ApiModelProperty(value = "施工计划")
    private String constructionPlan;

    @TableField(value = "safety_requirements")
    @ApiModelProperty(value = "安全要求")
    private String safetyRequirements;

    @TableField(value = "quality_requirements")
    @ApiModelProperty(value = "质量要求")
    private String qualityRequirements;

    @TableField(value = "technical_requirements")
    @ApiModelProperty(value = "技术要求")
    private String technicalRequirements;

    @TableField(value = "estimated_cost")
    @ApiModelProperty(value = "预估总成本", required = "required,num")
    private String estimatedCost;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "计划开始日期")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "计划结束日期")
    private String endTime;

    @TableField(value = "responsible_person")
    @ApiModelProperty(value = "负责人")
    private String responsiblePerson;

    @TableField(exist = false)
    @Property(value = "负责人信息")
    private Map<String, Object> responsiblePersonMation;

    @TableField(value = "contact_phone")
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @Property(value = "施工步骤列表")
    private List<ProConstructionStep> constructionStepList;

    @TableField(exist = false)
    @Property(value = "项目材料清单")
    private List<ProConstructionMaterial> constructionMaterialList;

}