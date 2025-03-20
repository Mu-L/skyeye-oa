package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.school.score.classenum.WorkTypeEnum;
import lombok.Data;

import java.util.Map;

@Data
@TableName("school_score_part")
@ApiModel(value = "成绩部分实体类")
public class ScorePart extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField("work_id")
    @ApiModelProperty(value = "任务id,当自定义成绩时，此属性作为任务名称使用", required = "required")
    private String workId;

    @TableField(exist = false)
    @ApiModelProperty(value = "任务信息")
    private Map<String, Object> workMation;

    @TableField("work_type")
    @ApiModelProperty(value = "任务类型", enumClass = WorkTypeEnum.class, required = "required")
    private Integer workType;

    @TableField("score")
    @ApiModelProperty(value = "成绩")
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
