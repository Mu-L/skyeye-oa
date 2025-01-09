package com.skyeye.exam.examquorderby.entity;

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
@RedisCacheField(name = "Exam:orderby")
@TableName(value = "exam_qu_orderby")
@ApiModel("排序题行选项实体类")
public class ExamQuOrderby extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("qu_id")
    @ApiModelProperty(value = "所属题")
    private String quId;

    @TableField("option_name")
    @ApiModelProperty(value = "选项内容", required = "required")
    private String optionName;

    @TableField(exist = false)
    @ApiModelProperty(value = "所属题")
    private String optionId;

    @TableField("option_title")
    @ApiModelProperty(value = "标识")
    private String optionTitle;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序号")
    private Integer orderById;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示", required = "required")
    private Integer visibility;

}