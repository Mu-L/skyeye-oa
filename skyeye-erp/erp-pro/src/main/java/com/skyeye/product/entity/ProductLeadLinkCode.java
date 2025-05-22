package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.SkyeyeLinkData;
import lombok.Data;

@Data
@TableName(value = "erp_product_lead_goods")
@ApiModel("借出出库子表关联的条形码编号实体类")
public class ProductLeadLinkCode extends CommonInfo {

    @TableId(value = "id")
    private String id;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "单据id", required = "required")
    private String parentId;

    @TableField(value = "article_id")
    @ApiModelProperty(value = "产品id", required = "required,double")
    private String articleId;

    @TableField(value = "norms_code")
    @ApiModelProperty(value = "条形码编号")
    private String normsCode;
}
