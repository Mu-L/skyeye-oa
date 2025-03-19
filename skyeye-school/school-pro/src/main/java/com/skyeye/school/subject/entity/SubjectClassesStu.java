/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: SubjectClassesStu
 * @Description: 科目表与班级表关系下的学生信息实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "school_subject_classes_stu")
@ApiModel(value = "科目表与班级表关系下的学生信息实体类")
public class SubjectClassesStu extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("sub_class_link_id")
    @ApiModelProperty(value = "科目表与班级表关系id", required = "required")
    private String subClassLinkId;

    @TableField("stu_no")
    @Property(value = "学生学号")
    private String stuNo;

    @TableField("join_time")
    @Property(value = "加入时间")
    private String joinTime;

    @TableField("reward")
    @ApiModelProperty(value = "奖励星星",defaultValue = "0")
    private String reward;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生信息")
    private Map<String, Object> stuInfo;

}
