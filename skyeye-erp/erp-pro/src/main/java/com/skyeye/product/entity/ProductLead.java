package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@TableName(value = "erp_product_lead")
@ApiModel("借出申请实体类")
public class ProductLead extends SkyeyeFlowable {

    @TableField("title")
    @ApiModelProperty(value = "单据主题", required = "required")
    private String title;

    @TableField("oper_time")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String operTime;

    @TableField("holder_id")
    @ApiModelProperty(value = "关联的客户/供应商/会员id")
    private String holderId;

    @TableField(exist = false)
    @ApiModelProperty(value = "关联的客户/供应商/会员信息")
    private Map<String, Object> holderMation;

    @TableField("total_price")
    @ApiModelProperty(value = "总价钱")
    private String totalPrice;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "借出申请明细信息", required = "required,json")
    private List<ProductLeadChild> erpOrderItemList;

}
