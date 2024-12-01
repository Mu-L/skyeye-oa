package com.skyeye.exam.examQuRadio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.question.entity.Question;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ExamQuRadio
 * @Description: 单选题选项表实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "exam_qu_radio")
@ApiModel("单选题选项表实体类")
public class ExamQuRadio extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题")
    private String quId;

    @TableField("option_name")
    @ApiModelProperty(value = "选项内容", required = "required")
    private String optionName;

    @TableField("option_id")
    @ApiModelProperty(value = "选项id")
    private String optionId;

    @TableField("option_title")
    @ApiModelProperty(value = "选项标题",required = "required")
    private String optionTitle;

    @TableField("check_type")
    @ApiModelProperty(value = "说明的验证方式")
    private Integer checkType;

    @TableField("is_note")
    @ApiModelProperty(value = "是否带说明  0否  1是")
    private Integer isNote;

    @TableField("is_required_fill")
    @ApiModelProperty(value = "说明内容是否必填 0非必填  1必填", required = "required")
    private Integer isRequiredFill;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID", required = "required")
    private Integer orderById;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示", required = "required")
    private Integer visibility;

    @TableField("is_default_answer")
    @ApiModelProperty(value = "是否是默认答案  1.是  2.否")
    private Integer isDefaultAnswer;


}