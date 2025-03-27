package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
@TableName(value = "school_score_max_min")
@ApiModel(value = "班级最高分和最低分记录实体类")
public class ScoreMaxMin extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField("class_id")
    @ApiModelProperty(value = "班级id", required = "required")
    private String classId;

    @TableField("max_score")
    @ApiModelProperty(value = "最高分", required = "required")
    private String maxScore;

    @TableField("min_score")
    @ApiModelProperty(value = "最低分", required = "required")
    private String minScore;
}
