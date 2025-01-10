package com.skyeye.exam.examquchenoption.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
@RedisCacheField(name = "Exam:chenoption")
@TableName(value = "exam_qu_chen_option")
@ApiModel("矩陈题-题选项实体类")
public class ExamQuChenOption extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属题库或试卷")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题", required = "required")
    private String quId;

    @TableField("option_name")
    @ApiModelProperty(value = "选项内容", required = "required")
    private Long optionName;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID", required = "required")
    private Integer orderById;

    @TableField(exist = false)
    @ApiModelProperty(value = "选项id")
    private String optionId;
}