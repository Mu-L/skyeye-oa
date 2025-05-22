package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "erp_product_lead")
@ApiModel("借出出库实体类")
public class ProductLead extends SkyeyeFlowable {

    @TableField(value = "id_key", updateStrategy = FieldStrategy.NEVER)
    @Property("服务类的serviceClassName")
    private String idKey;

    @TableField(value = "contact_units")
    @ApiModelProperty(value = "往来单位（客户 、供应商）")
    private String contactUnits;

    @TableField(value = "document_date")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String documentDate;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "总金额")
    private String allPrice;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "借出出库产品信息", required = "required,json")
    private List<ProductLeadLink> productLeadLinks;

}
