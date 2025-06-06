package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.material.entity.Material;
import com.skyeye.material.entity.MaterialNorms;
import lombok.Data;

@Data
@TableName(value = "erp_product_lead_child")
@ApiModel("借出申请表-子单据表实体类")
public class ProductLeadChild extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("parent_id")
    @Property("单据id")
    private String parentId;

    @TableField("material_id")
    @ApiModelProperty(value = "产品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "产品信息")
    private Material materialMation;

    @TableField("norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总金额", defaultValue = "0")
    private String allPrice;

    @TableField("number")
    @ApiModelProperty(value = "数量", required = "required,num")
    private Integer number;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private MaterialNorms normsMation;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价", required = "double", defaultValue = "0")
    private String unitPrice;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;
}
