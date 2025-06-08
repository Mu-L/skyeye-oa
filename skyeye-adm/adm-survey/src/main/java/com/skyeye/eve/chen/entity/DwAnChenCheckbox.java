/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.eve.chen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DwAnChenCheckbox
 * @Description: 答卷矩阵多选题实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Data
@TableName(value = "dw_an_chen_checkbox")
@ApiModel(value = "答卷矩阵多选题实体类")
public class DwAnChenCheckbox extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("belong_answer_id")
    @ApiModelProperty(value = "对应的答卷信息表")
    private String belongAnswerId;

    @TableField("belong_id")
    @ApiModelProperty(value = "所属问卷ID")
    private String belongId;

    @TableField("qu_id")
    @ApiModelProperty(value = "问题ID")
    private String quId;

    @TableField("qu_col_id")
    @ApiModelProperty(value = "矩阵列选项ID")
    private String quColId;

    @TableField("qu_row_id")
    @ApiModelProperty(value = "矩阵行选项ID")
    private String quRowId;

    @TableField("visibility")
    @ApiModelProperty(value = "1 是 0非")
    private Integer visibility;

    @TableField(exist = false)
    @ApiModelProperty(value = "矩阵题-矩阵多选题信息答案")
    private List<DwAnChenCheckbox> dwChenCheckboxAn;


}