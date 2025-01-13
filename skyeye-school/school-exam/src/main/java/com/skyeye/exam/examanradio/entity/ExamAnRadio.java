package com.skyeye.exam.examanradio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

@Data
//@RedisCacheField(name = "Exam:radio")
@TableName(value = "exam_an_radio")
@ApiModel("单选题保存表实体类")
public class ExamAnRadio extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "对应的答卷信息表", required = "required")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷ID", required = "required")
    private String belongId;

    @TableField("other_text")
    @ApiModelProperty(value = "复合题的其它项")
    private String otherText;

    @TableField("qu_id")
    @ApiModelProperty(value = "题目 ID", required = "required")
    private String quId;

    @TableField("qu_item_id")
    @ApiModelProperty(value = "结果选项ID", required = "required")
    private String quItemId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;
}