package com.skyeye.construction.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ProConstructionStep
 * @Description: 施工步骤实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "pro_construction_step")
@ApiModel("施工步骤实体类")
public class ProConstructionStep extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "construction_id")
    @ApiModelProperty(value = "施工方案id", required = "required")
    private String constructionId;

    @TableField(exist = false)
    @Property(value = "施工方案信息")
    private ProConstruction constructionMation;

    @TableField(value = "step_name")
    @ApiModelProperty(value = "步骤名称", required = "required")
    private String stepName;

    @TableField(value = "step_content")
    @ApiModelProperty(value = "步骤内容", required = "required")
    private String stepContent;

    @TableField(value = "step_order")
    @ApiModelProperty(value = "步骤顺序", required = "required,num")
    private Integer stepOrder;

    @TableField(value = "estimated_days")
    @ApiModelProperty(value = "预估天数", required = "required,num")
    private String estimatedDays;

    @TableField(value = "responsible_person")
    @ApiModelProperty(value = "负责人")
    private String responsiblePerson;

    @TableField(value = "required_skills")
    @ApiModelProperty(value = "所需技能")
    private String requiredSkills;

    @TableField(value = "safety_notes")
    @ApiModelProperty(value = "安全注意事项")
    private String safetyNotes;

    @TableField(value = "quality_requirements")
    @ApiModelProperty(value = "质量要求")
    private String qualityRequirements;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}