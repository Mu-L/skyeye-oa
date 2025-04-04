package com.skyeye.exam.examanyesno.entity;

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
//@RedisCacheField(name = "Exam:yesno")
@TableName(value = "exam_an_yesno")
@ApiModel("判断题实体类")
public class ExamAnYesno extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "对应的答卷信息表", required = "required")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷ID", required = "required")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "问题ID", required = "required")
    private String quId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField("yesno_answer")
    @ApiModelProperty(value = "1是 0非")
    private String yesnoAnswer;
}