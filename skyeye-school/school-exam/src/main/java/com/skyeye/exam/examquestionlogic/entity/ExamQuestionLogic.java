package com.skyeye.exam.examquestionlogic.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.base.handler.enclosure.bean.EnclosureFace;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: ExamQuestionLogic
 * @Description: 题目逻辑设置实体类
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "exam_question_logic")
@ApiModel("题目逻辑设置实体类")
public class ExamQuestionLogic extends OperatorUserInfo implements EnclosureFace {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("title")
    @ApiModelProperty(value = "标题")
    private String title;

    @TableField("cg_qu_item_id")
    @ApiModelProperty(value = "回答选择题的选项ID  （0任意选项）")
    private String cgQuItemId;

    @TableField("ck_qu_id")
    @ApiModelProperty(value = "回答选择的题ID", required = "required")
    private String ckQuId;

    @TableField("ge_le")
    @ApiModelProperty(value = "评分题 ge大于，le小于")
    private String geLe;

    @TableField("logic_type")
    @ApiModelProperty(value = "逻辑类型  (1=跳转,2显示)", required = "required")
    private Integer logicType;

    @TableField("score_num")
    @ApiModelProperty(value = "分数")
    private Integer scoreNum;

    @TableField("sk_qu_id")
    @ApiModelProperty(value = "要跳转的题  (end1提前结束-计入结果  end2提前结束-不计结果)")
    private String skQuId;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  1显示 0不显示")
    private Integer visibility;

}