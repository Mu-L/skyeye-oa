package com.skyeye.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.Map;

@Data
@TableName("shop_order_comment")
@ApiModel("商品订单评价管理实体类")
public class OrderComment extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id")
    private String id;

    @TableField(value = "parent_id")
    @ApiModelProperty(value = "父id")
    private String parentId;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "order_id")
    @ApiModelProperty(value = "订单id", required = "required")
    private String orderId;

    @TableField(value = "order_item_id")
    @ApiModelProperty(value = "订单子单id", required = "required")
    private String orderItemId;

    @TableField(value = "type")
    @ApiModelProperty(value = "类型,参考#OrderCommentType")
    private Integer type;

    @TableField(value = "context")
    @ApiModelProperty(value = "评价内容", required = "required")
    private String context;
}
