package com.skyeye.exam.examquchenrow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

@Data
//@RedisCacheField(name = "Exam:chenrow")
@TableName(value = "exam_qu_chen_row")
@ApiModel("矩阵题-行选项实体类")
public class ExamQuChenRow extends OperatorUserInfo  {

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

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID")
    private Integer orderById;

    @TableField("order_by")
    @ApiModelProperty(value = "排序")
    private Integer orderBy;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField(exist = false)
    @ApiModelProperty(value = "选项id")
    private String optionId;

}