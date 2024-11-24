/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

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

/**
 * @ClassName: OrderComment
 * @Description: 商品订单评价管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
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

    @TableField(value = "start")
    @ApiModelProperty(value = "星级(1-5)",required = "required")
    private Integer start;

    @TableField(value = "context")
    @ApiModelProperty(value = "评价内容", required = "required")
    private String context;
}
