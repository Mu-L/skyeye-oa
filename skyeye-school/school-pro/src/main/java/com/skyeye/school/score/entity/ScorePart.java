package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@TableName("school_score_part")
@ApiModel(value = "成绩部分实体类")
public class ScorePart extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("score")
    @ApiModelProperty(value = "成绩", required = "required")
    private String score;

    @TableField("proportion")
    @ApiModelProperty(value = "占比", required = "required")
    private String proportion;

    @TableField("stu_no")
    @ApiModelProperty(value = "学号", required = "required")
    private String stuNo;

    @TableField("object_id")
    @ApiModelProperty(value = "第三方业务数据id(成绩类型主表id或成绩类型子表id)", required = "required")
    private String objectId;
}
