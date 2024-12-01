package com.skyeye.exam.examQuScore.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ExamQuScore
 * @Description: 评分题行选项实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "Exam:score")
@TableName(value = "exam_qu_score")
@ApiModel("评分题行选项实体类")
public class ExamQuScore extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题")
    private String quId;

    @TableField("qu_type")
    @ApiModelProperty(value = "题目类型", required = "required")
    private Integer quType;

    @TableField("option_name")
    @ApiModelProperty(value = "选项内容", required = "required")
    private String optionName;

    @TableField("option_id")
    @ApiModelProperty(value = "选项id")
    private String optionId;

    @TableField("option_title")
    @ApiModelProperty(value = "标识")
    private String optionTitle;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序号", required = "required")
    private Integer orderById;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示", required = "required")
    private Integer visibility;

    @TableField(exist = false)
    @ApiModelProperty(value = "评分题选项信息", required = "json")
    private List<ExamQuScore> scoreTd;
}