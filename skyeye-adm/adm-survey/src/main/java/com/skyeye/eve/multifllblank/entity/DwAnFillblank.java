package com.skyeye.eve.multifllblank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import javax.activation.CommandInfo;

@Data
@UniqueField
@TableName(value = "dw_an_fillblank")
@ApiModel(value = "答卷多选题保存表实体类")
public class DwAnFillblank extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("answer")
    @ApiModelProperty(value = "答案")
    private String answer;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "对应的答卷信息表")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷ID")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "问题ID")
    private String quId;

    @TableField("visibility")
    @ApiModelProperty(value = "1 是 0非")
    private Integer visibility;
}
