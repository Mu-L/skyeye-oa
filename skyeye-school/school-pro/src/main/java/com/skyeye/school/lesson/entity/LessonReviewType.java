package com.skyeye.school.lesson.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@TableName(value = "school_lesson review_type")
@ApiModel(value = "听评表角色管理实体类")
public class LessonReviewType extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("parent_id")
    @ApiModelProperty(value = "父类Id")
    private String parentId;

    @TableField("class_hour")
    @ApiModelProperty(value = "学时")
    private Integer classHour;

    @TableField("name")
    @ApiModelProperty(value = "类型名称")
    private String name;

    @TableField("model_id")
    @ApiModelProperty(value = "模型id")
    private String modelId;

    @TableField("lectures_role_id")
    @ApiModelProperty(value = "关联角色id")
    private String lecturesRoleId;
}
