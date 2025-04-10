/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
@TableName(value = "school_groups_student")
@ApiModel(value = "学生与分组关系表实体类")
public class GroupsStudent extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("student_number")
    @ApiModelProperty(value = "学生学号", required = "required")
    private String studentNumber;

    @TableField("group_id")
    @ApiModelProperty(value = "分组id", required = "required")
    private String groupId;

    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @TableField(exist = false)
    @Property("分组信息")
    private Groups groupsMation;
}
