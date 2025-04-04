package com.skyeye.exam.examqumultfillblank.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.examquestion.classenum.CheckTypes;
import lombok.Data;

@Data
//@RedisCacheField(name = "Exam:multifillblank")
@TableName(value = "exam_qu_multi_fillblank")
@ApiModel("多行填空题实体类")
public class ExamQuMultiFillblank extends OperatorUserInfo {

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
    @ApiModelProperty(value = "选项标题")
    private String optionTitle;

    @TableField("check_type")
    @ApiModelProperty(value = "说明的验证方式", enumClass = CheckTypes.class)
    private Integer checkType;

    @TableField("order_by_id")
    @ApiModelProperty(value = "排序ID")
    private Integer orderById;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

    @TableField("is_default_answer")
    @ApiModelProperty(value = "是否是默认答案  1.是  2.否")
    private String isDefaultAnswer;

    @TableField(exist = false)
    @ApiModelProperty(value = "选项id")
    private String optionId;
}