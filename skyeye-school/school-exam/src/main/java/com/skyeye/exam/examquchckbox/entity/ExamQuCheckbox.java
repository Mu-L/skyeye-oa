/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.exam.examquchckbox.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.exam.examquestion.classenum.CheckTypes;
import lombok.Data;

@Data
@TableName(value = "exam_qu_checkbox")
@ApiModel("多选题选项表实体类")
public class ExamQuCheckbox extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属题库或试卷")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题")
    private String quId;

    @TableField("option_name")
    @ApiModelProperty(value = "选项内容", required = "required")
    private String optionName;

    @TableField("option_title")
    @ApiModelProperty(value = "所属题")
    private String optionTitle;

    @TableField(exist = false)
    @ApiModelProperty(value = "选项id")
    private String optionId;

    @TableField("check_type")
    @ApiModelProperty(value = "说明的验证方式", enumClass = CheckTypes.class)
    private Integer checkType;

    @TableField("is_note")
    @ApiModelProperty(value = "是否带说明  0否  1是", required = "required")
    private Integer isNote;

    @TableField("is_required_fill")
    @ApiModelProperty(value = "说明内容是否必填 0否  1是", required = "required")
    private Integer isRequiredFill;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID")
    private Integer orderById;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField("is_default_answer")
    @ApiModelProperty(value = "是否是默认答案  1.是  2.否", required = "required")
    private Integer isDefaultAnswer;
}