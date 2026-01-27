/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.scheme.enums.BydgetType;
import lombok.Data;

/**
 * @ClassName: ProSchemeBudgetDetail
 * @Description: 项目方案预算明细实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "pro_scheme_budget_detail")
@ApiModel("项目方案预算明细实体类")
public class ProSchemeBudgetDetail extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "scheme_id")
    @Property(value = "关联方案id")
    private String schemeId;

    @TableField(value = "budget_type")
    @ApiModelProperty(value = "预算类型", enumClass = BydgetType.class, required = "required")
    private String budgetType;

    @TableField(value = "specification")
    @ApiModelProperty(value = "规格说明")
    private String specification;

    @TableField(value = "quantity")
    @ApiModelProperty(value = "数量")
    private String quantity;

    @TableField(value = "unit")
    @ApiModelProperty(value = "单位")
    private String unit;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价")
    private String unitPrice;

    @TableField(value = "subtotal")
    @Property(value = "小计")
    private String subtotal;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(value = "order_by")
    @Property(value = "排序")
    private Integer orderBy;

}

