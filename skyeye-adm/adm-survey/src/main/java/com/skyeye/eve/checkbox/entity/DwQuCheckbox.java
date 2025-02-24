/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.checkbox.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;


/**
 * @ClassName: DwQuCheckbox
 * @Description: 多选题选项实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "dw_qu_checkbox")
@ApiModel(value = "多选题选项实体类")
public class DwQuCheckbox extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题", required = "required")
    private String quId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷id")
    private String belongId;

    @TableField("option_name")
    @ApiModelProperty(value = "选项名称", required = "required")
    private String optionName;

    @TableField("option_title")
    @ApiModelProperty(value = "选项标题")
    private String optionTitle;

    @TableField(exist = false)
    @ApiModelProperty(value = "选项id")
    private String optionId;

    @TableField("check_type")
    @ApiModelProperty(value = "说明的验证方式")
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
    @ApiModelProperty(value = "是否显示  0不显示  1显示", required = "required")
    private Integer visibility;

    @TableField("is_default_answer")
    @ApiModelProperty(value = "是否是默认答案  1.是  2.否", required = "required")
    private Integer isDefaultAnswer;
}