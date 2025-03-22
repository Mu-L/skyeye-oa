/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.question.entity;

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
 * @ClassName: DwQuestionLogic
 * @Description:题目逻辑设置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@UniqueField
@TableName(value = "dw_question_logic")
@ApiModel(value = "题目逻辑设置实体类")
public class DwQuestionLogic extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
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
    @ApiModelProperty(value = "是否显示  0不显示  1显示")
    private Integer visibility;

}

