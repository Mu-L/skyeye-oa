package com.skyeye.exam.examanscore.entity;

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

@Data
//@RedisCacheField(name = "Exam:score")
@TableName(value = "exam_an_score")
@ApiModel("评分题实体类")
public class ExamAnScore extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("answser_score")
    @ApiModelProperty(value = "答案分数")
    private Float answserScore;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属评分题答案id", required = "required")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属评分题id", required = "required")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属评分题", required = "required")
    private String quId;

    @TableField("qu_row_id")
    @ApiModelProperty(value = "所属评分题行id", required = "required")
    private String quRowId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField(exist = false)
    @ApiModelProperty(value = "评分题答案信息")
    private List<ExamAnScore> scoreAn;

}