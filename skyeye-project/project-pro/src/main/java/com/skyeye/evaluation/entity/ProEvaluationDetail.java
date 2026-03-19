package com.skyeye.evaluation.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.evaluation.classenum.EvaluationItemTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: ProEvaluationDetail
 * @Description: 项目评估明细实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "pro_evaluation_detail")
@ApiModel("项目评估明细实体类")
public class ProEvaluationDetail extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "evaluation_id")
    @ApiModelProperty(value = "评估id")
    private String evaluationId;

    @TableField(exist = false)
    @Property(value = "评估信息")
    private ProEvaluation evaluationMation;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "评估项名称", required = "required")
    private String name;

    @TableField(value = "evaluation_type")
    @ApiModelProperty(value = "评估项类型", enumClass = EvaluationItemTypeEnum.class, required = "required")
    private String evaluationType;

    /**
     * 权重：表示该评估项在总体评估中的重要程度
     * 例如：技术难度权重20%，资源需求权重20%，时间风险权重20%，成本风险权重20%，质量风险权重20%
     * 权重总和应为100%
     */
    @TableField(value = "weight")
    @ApiModelProperty(value = "权重(%) - 表示该评估项的重要程度", required = "required,num")
    private BigDecimal weight;

    /**
     * 评分：对该评估项的具体评分，范围1-10分
     * 1分：很差/很高风险  10分：很好/很低风险
     */
    @TableField(value = "score")
    @ApiModelProperty(value = "评分(1-10分) - 越高表示表现越好/风险越低", required = "required,num")
    private Integer score;

    /**
     * 加权得分：权重 × 评分
     * 例如：权重20%，评分8分，加权得分 = 20% × 8 = 1.6分
     * 用于计算综合评分
     */
    @TableField(value = "weighted_score")
    @ApiModelProperty(value = "加权得分 - 权重×评分，用于综合评分计算", required = "required,num")
    private BigDecimal weightedScore;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序")
    private Integer orderBy;

}