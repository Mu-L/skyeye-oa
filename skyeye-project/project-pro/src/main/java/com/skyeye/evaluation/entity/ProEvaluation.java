package com.skyeye.evaluation.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.scheme.entity.ProScheme;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ProEvaluation
 * @Description: 项目评估实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "pm:projectEvaluation", cacheTime = RedisConstants.TOW_MONTH_SECONDS)
@TableName(value = "pro_evaluation")
@ApiModel("项目评估实体类")
public class ProEvaluation extends SkyeyeFlowable {

    @TableField("`name`")
    @ApiModelProperty(value = "评估名称", required = "required", fuzzyLike = true)
    private String name;

    @TableField(value = "project_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "项目id", required = "required")
    private String projectId;

    @TableField(exist = false)
    @Property(value = "项目信息")
    private Object projectMation;

    @TableField(value = "scheme_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "方案id", required = "required")
    private String schemeId;

    @TableField(exist = false)
    @Property(value = "方案信息")
    private ProScheme schemeMation;

    @TableField(value = "evaluation_content")
    @ApiModelProperty(value = "评估内容")
    private String evaluationContent;

    @TableField(value = "evaluation_suggestion")
    @ApiModelProperty(value = "评估建议")
    private String evaluationSuggestion;

    @TableField(value = "risk_assessment")
    @ApiModelProperty(value = "风险评估")
    private String riskAssessment;

    @TableField(value = "recommendations")
    @ApiModelProperty(value = "改进建议")
    private String recommendations;

    @TableField(exist = false)
    @ApiModelProperty(value = "评估明细列表", required = "required,json")
    private List<ProEvaluationDetail> evaluationDetailList;

}