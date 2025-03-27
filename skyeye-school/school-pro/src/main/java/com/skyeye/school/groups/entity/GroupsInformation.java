package com.skyeye.school.groups.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@RedisCacheField(name = "school:GroupsInformation", cacheTime = RedisConstants.HALF_A_YEAR_SECONDS)
@TableName(value = "school_groups_information")
@ApiModel(value = "学生分组信息实体类")
public class GroupsInformation extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("name")
    @ApiModelProperty(value = "分组名称（学习小组，临时小组）")
    private String name;

    @TableField("status")
    @ApiModelProperty(value = "0(按组分组) 1(按人数分组)",required = "required")//枚举
    private Integer status;

    @TableField("join_groups_stu")
    @ApiModelProperty(value = "已加组的学生人数")
    private Integer joinGroupsStu;

    @TableField("gro_number")
    @ApiModelProperty(value = "需按组分组数量")
    private Integer groNumber;

    @TableField("groups_num")
    @ApiModelProperty(value = "需按人数分组的数量")
    private Integer groupsNum;

    @TableField("groups_number")
    @ApiModelProperty(value = "最终得到人数分组数量")
    private Integer groupsNumber;

    @TableField("subject_id")
    @ApiModelProperty(value = "科目ID")
    private String subjectId;

    @TableField("class_id")
    @ApiModelProperty(value = "班级ID")
    private String classId;

}
