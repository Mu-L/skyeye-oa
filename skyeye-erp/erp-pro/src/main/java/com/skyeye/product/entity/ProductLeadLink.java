package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "erp_product_lead_goods")
@ApiModel("借出出库申请关联的产品价格信息实体类")
public class ProductLeadLink extends SkyeyeLinkData {

    @TableField(value = "article_id")
    @ApiModelProperty(value = "用品id", required = "required")
    private String articleId;

    @TableField(value = "article_num")
    @ApiModelProperty(value = "借出数量", required = "required")
    private String articleNum;

    @TableField(value = "unit_price")
    @ApiModelProperty(value = "单价", required = "required,double")
    private String unitPrice;

    @TableField(value = "all_price")
    @ApiModelProperty(value = "金额")
    private String allPrice;

    @TableField(exist = false)
    @Property(value = "产品条形码编号集合")
    private List<String> productLeadCodeList;

    @TableField(exist = false)
    @Property(value = "借出出库产品条形码编号")
    private List<ProductLeadLinkCode> leadCodeMation;

}
