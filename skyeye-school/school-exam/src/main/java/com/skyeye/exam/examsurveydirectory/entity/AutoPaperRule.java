package com.skyeye.exam.examsurveydirectory.entity;

import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: AutoPaperRule
 * @Description: 自动组卷规则实体类
 * @author: skyeye云系列
 * @date: 2024/12/01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel(value = "自动组卷规则")
public class AutoPaperRule {

    @ApiModelProperty(value = "题目类型", required = "required")
    private Integer quType;

    @ApiModelProperty(value = "题目数量", required = "required")
    private Integer questionCount;

    @ApiModelProperty(value = "总分值", required = "required")
    private Integer totalScore;

    @ApiModelProperty(value = "题库ID，如果不指定则从所有题库中选择")
    private String belongId;

    @ApiModelProperty(value = "科目ID，用于筛选题目")
    private String subjectId;

    @ApiModelProperty(value = "知识点ID，多个用逗号分隔，用于筛选题目")
    private String knowledgeIds;
}

