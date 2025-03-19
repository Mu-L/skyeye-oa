package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@TableName(value = " score")
@ApiModel(value = "成绩实体类")
public class Score extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("no")
    @ApiModelProperty(value = "学号", required = "required")
    private String no;

    @TableField("class_id")
    @ApiModelProperty(value = "所属班级", required = "required")
    private String classId;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField("semester_id")
    @ApiModelProperty(value = "学期id", required = "required")
    private String semesterId;

    @TableField("grade")
    @ApiModelProperty(value = "成绩", required = "required")
    private Integer grade;

    @TableField("object_id")
    @ApiModelProperty(value = "所属对象id")
    private String objectId;

    @TableField("object_key")
    @ApiModelProperty(value = "所属对象类型")
    private String objectKey;

}
