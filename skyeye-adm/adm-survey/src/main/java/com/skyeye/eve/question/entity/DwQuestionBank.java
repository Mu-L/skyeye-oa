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
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

/**
 * @ClassName: DwQuestionBank
 * @Description:题库实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField
@TableName(value = "dw_question_bank")
@ApiModel(value = "题库实体类")
public class DwQuestionBank extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("bank_name")
    @ApiModelProperty(value = "题库名称", required = "required")
    private String bankName;

    @TableField("bank_note")
    @ApiModelProperty(value = "说明")
    private String bankNote;

    @TableField("bank_state")
    @ApiModelProperty(value = "状态 0设计状态  1发布状态", required = "required")
    private Integer bankState;

    @TableField("bank_tag")
    @ApiModelProperty(value = "共享题库 0 官方库  1用户共享  2用户自己的库", required = "required")
    private Integer bankTag;

    @TableField("dir_type")
    @ApiModelProperty(value = "1目录，2题库", required = "required")
    private Integer dirType;

    @TableField("parent_id")
    @ApiModelProperty(value = "dir_type为2时有此参数")
    private String parentId;

    @TableField("excerpt_num")
    @ApiModelProperty(value = "引用次数", required = "required")
    private Integer excerptNum;

    @TableField("group_id1")
    @ApiModelProperty(value = "问卷所属的组  功能分组")
    private String groupId1;

    @TableField("group_id2")
    @ApiModelProperty(value = "分组2\t行业分组")
    private String groupId2;

    @TableField("qu_num")
    @ApiModelProperty(value = "题目数", required = "required")
    private Integer quNum;

    @TableField("visibility")
    @ApiModelProperty(value = "是否显示  0不显示  1显示", required = "required")
    private Integer visibility;

}

