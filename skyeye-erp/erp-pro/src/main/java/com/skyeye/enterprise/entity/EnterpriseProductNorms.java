/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: EnterpriseProductNorms
 * @Description: 企业商品规格参数实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "erp_enterprise_product_norms")
@ApiModel("企业商品规格参数实体类")
public class EnterpriseProductNorms extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "enterprise_product_id")
    @ApiModelProperty(value = "企业商品id")
    private String enterpriseProductId;

    @TableField(exist = false)
    @Property(value = "企业商品信息")
    private EnterpriseProduct enterpriseProductMation;

    @TableField(value = "`name`")
    @ApiModelProperty(value = "规格名称")
    private String name;

    @TableField(value = "logo")
    @ApiModelProperty(value = "商品规格图片")
    private String logo;

    @TableField(value = "table_num")
    @ApiModelProperty(value = "商品规格编号，同一个商品下唯一。多规格的商品具备该字段")
    private String tableNum;

    @TableField(value = "price")
    @ApiModelProperty(value = "销售价", required = "required")
    private BigDecimal price;

    @TableField(value = "estimate_purchase_price")
    @ApiModelProperty(value = "成本价")
    private BigDecimal estimatePurchasePrice;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序")
    private Integer orderBy;

}