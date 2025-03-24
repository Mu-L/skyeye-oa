package com.skyeye.school.score.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.school.score.classenum.NumberCodeEnum;
import lombok.Data;

import java.util.List;

@Data
@TableName("school_score_type_child")
@ApiModel(value = "成绩类型子表实体类")
public class ScoreTypeChild extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "number_code")
    @Property(value = "类型编码", enumClass = NumberCodeEnum.class)
    private Integer numberCode;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目id", required = "required")
    private String subjectId;

    @TableField("class_id")
    @ApiModelProperty(value = "班级id", required = "required")
    private String classId;

    @TableField("is_default")
    @Property(value = "类型(是否默认，默认不可删除)", enumClass = IsDefaultEnum.class)
    private Integer isDefault;

    @TableField("name")
    @ApiModelProperty(value = "名称")
    private String name;

    @TableField("proportion")
    @ApiModelProperty(value = "占比(80即占比80%)",required = "required")
    private String proportion;

    @TableField("score_type_id")
    @ApiModelProperty(value = "成绩类型id")
    private String scoreTypeId;

    @TableField("parent_id")
    @ApiModelProperty(value = "父级id(成绩类型主表id)")
    private String parentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "成绩列表")
    private List<ScoreSum> scoreSumList;
}
