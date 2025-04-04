package com.skyeye.exam.examanchenfbk.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ExamSurveyAnswer
 * @Description: 答卷 矩阵填空题实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ApiModel("答卷 矩阵填空题")
public class ExamAnChenFbk extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("answer_value")
    @ApiModelProperty(value = "矩阵填空题答案值")
    private String answerValue;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属矩阵填空题答案id")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属试卷id")
    private String belongId;

    @TableField("qu_col_id")
    @ApiModelProperty(value = "所属矩阵填空题")
    private String quColId;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属问答题答案id")
    private String quId;

    @TableField("qu_row_id")
    @ApiModelProperty(value = "所属矩阵填空题行id")
    private String quRowId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField(exist = false)
    @ApiModelProperty(value = "矩阵题-矩阵填空题信息答案")
    private List<ExamAnChenFbk> chenFbkAn;

}
