package com.skyeye.eve.enumqu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: DwAnEnumqu
 * @Description: 答卷 枚举题答案实体类
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
//@RedisCacheField(name = "Exam:enumqu")
@TableName(value = "dw_an_enumqu")
@ApiModel("答卷 枚举题答案实体类")
public class DwAnEnumqu extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("answer")
    @ApiModelProperty(value = "枚举题答案")
    private String answer;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "所属枚举题答案id")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属试卷id")
    private String belongId;

    @TableField("enum_item")
    @ApiModelProperty(value = "第几个枚举项")
    private Integer enumItem;

    @TableField("qu_id")
    @ApiModelProperty(value = "问题ID")
    private String quId;

    @TableField("visibility")
    @ApiModelProperty(value = "1 是 0非")
    private Integer visibility;
}
