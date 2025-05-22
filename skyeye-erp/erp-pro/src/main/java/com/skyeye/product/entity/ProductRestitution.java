package com.skyeye.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.entity.features.SkyeyeFlowable;
import lombok.Data;

@Data
@TableName(value = "erp_product_restitution")
@ApiModel("归还入库实体类")
public class ProductRestitution extends SkyeyeFlowable {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "contact_units")
    @ApiModelProperty(value = "往来单位（客户 、供应商）")
    private String contactUnits;

    @TableField(value = "document_date")
    @ApiModelProperty(value = "单据日期", required = "required")
    private String documentDate;

    @TableField(value = "lead_id")
    @ApiModelProperty(value = "借出出库id", required = "required")
    private String leadId;

    @TableField(value = "goods_id")
    @ApiModelProperty(value = "产品id", required = "required")
    private String goodsId;

    @TableField(value = "count_number")
    @ApiModelProperty(value = "产品总数量")
    private Integer countNumber;

    @TableField(value = "returned_number")
    @ApiModelProperty(value = "已归还数量")
    private Integer returnedNumber;

    @TableField(value = "unreturned_number")
    @ApiModelProperty(value = "未归还数量")
    private Integer unreturnedNumber;

    @TableField(value = "total_amount")
    @ApiModelProperty(value = "总金额")
    private Float totalAmount;

    @TableField(value = "process_instance_id")
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    @TableField(value = "inbound_warehouse_id")
    @ApiModelProperty(value = "入仓仓库id")
    private String inboundWarehouseId;

    @TableField(value = "barcode")
    @ApiModelProperty(value = "条形码编号")
    private String barcode;

    @TableField(value = "specifications")
    @ApiModelProperty(value = "规格")
    private String specifications;

    @TableField(value = "remark")
    @ApiModelProperty(value = "备注")
    private String remark;

}
