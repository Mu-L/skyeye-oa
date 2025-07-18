package com.skyeye.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.delivery.entity.ShopDeliveryCompany;
import com.skyeye.order.enums.ItemDeliverHistoryState;
import lombok.Data;

/**
 * @ClassName: ItemDeliverHistory
 * @Description: 商品订单单子项快递信息管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@UniqueField(value = {"deliverNumber"})
@TableName(value = "shop_order_item_deliver_history")
@ApiModel("商品订单单子项快递信息管理实体类")
public class ItemDeliverHistory extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "order_id")
    @ApiModelProperty(value = "订单id", required = "required")
    private String orderId;

    @TableField(value = "order_item_id")
    @ApiModelProperty(value = "订单id", required = "required")
    private String orderItemId;

    @TableField(exist = false)
    @Property(value = "订单单子项信息")
    private OrderItem orderItemMation;

    @TableField(value = "deliver_company_id")
    @ApiModelProperty(value = "快递公司信息id", required = "required")
    private String deliverCompanyId;

    @TableField(exist = false)
    @Property(value = "快递公司信息")
    private ShopDeliveryCompany deliverCompanyMation;

    @TableField(value = "deliver_template_charge_id")
    @ApiModelProperty(value = "快递计费模板id", required = "required")
    private String deliverTemplateChargeId;

    @TableField(value = "deliver_number")
    @ApiModelProperty(value = "快递单号(唯一)", required = "required")
    private String deliverNumber;

    @TableField(value = "num")
    @ApiModelProperty(value = "商品数量")
    private String num;

    @TableField(value = "price")
    @ApiModelProperty(value = "总价")
    private String price;

    @TableField(value = "state")
    @ApiModelProperty(value = "状态", enumClass = ItemDeliverHistoryState.class)
    private Integer state;
}
