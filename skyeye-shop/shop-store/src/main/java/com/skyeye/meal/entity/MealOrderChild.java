/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: MealOrderChild
 * @Description: 套餐订单所选套餐实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/6 20:06
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "shop_meal_order_child")
@ApiModel("套餐订单所选套餐实体类")
public class MealOrderChild extends CommonInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("order_id")
    @Property(value = "订单id")
    private String orderId;

    @TableField("meal_id")
    @ApiModelProperty(value = "套餐ID", required = "required")
    private String mealId;

    @TableField(exist = false)
    @Property(value = "套餐信息")
    private ShopMeal mealMation;

    @TableField(exist = false)
    @Property(value = "套餐名称")
    private String name;

    @TableField("meal_price")
    @Property(value = "套餐金额")
    private String mealPrice;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "所属第三方业务数据id")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @Property(value = "所属第三方业务数据的key")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField(value = "material_id")
    @ApiModelProperty(value = "商品id", required = "required")
    private String materialId;

    @TableField(exist = false)
    @Property(value = "商品信息")
    private Map<String, Object> materialMation;

    @TableField(value = "norms_id")
    @ApiModelProperty(value = "规格id", required = "required")
    private String normsId;

    @TableField(exist = false)
    @Property(value = "规格信息")
    private Map<String, Object> normsMation;

    @TableField(value = "code_num", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "规格物品编码", fuzzyLike = true)
    private String codeNum;

    @TableField(exist = false)
    @Property(value = "规格物品编码信息")
    private Map<String, Object> codeNumMation;

    @TableField(value = "start_time")
    @ApiModelProperty(value = "套餐开始时间")
    private String startTime;

    @TableField(value = "end_time")
    @ApiModelProperty(value = "套餐结束时间")
    private String endTime;

    @TableField(value = "state")
    @Property(value = "是否可用，参考#WhetherEnum")
    private Integer state;

    @TableField(exist = false)
    @Property(value = "是否下达退款单，参考#WhetherEnum")
    private Boolean isRefund;

    @TableField(exist = false)
    @Property(value = "退款单信息")
    private MealRefundOrder mealRefundOrder;

}
