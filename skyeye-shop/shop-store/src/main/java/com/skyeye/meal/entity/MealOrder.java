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
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.meal.classenum.ShopMealOrderState;
import com.skyeye.store.entity.ShopStore;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MealOrder
 * @Description: 套餐订单实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/6 20:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "shop:mealOrder", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "shop_meal_order")
@ApiModel("套餐订单实体类")
public class MealOrder extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("odd_number")
    @Property(value = "单据编号", fuzzyLike = true)
    private String oddNumber;

    @TableField(value = "object_id", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据id", required = "required")
    private String objectId;

    @TableField(value = "object_key", updateStrategy = FieldStrategy.NEVER)
    @ApiModelProperty(value = "所属第三方业务数据的key", required = "required")
    private String objectKey;

    @TableField(exist = false)
    @Property(value = "适用对象信息")
    private Map<String, Object> objectMation;

    @TableField("payable_price")
    @Property(value = "应付金额")
    private String payablePrice;

    @TableField("pay_price")
    @Property(value = "实付金额")
    private String payPrice;

    @TableField("pay_time")
    @Property(value = "实付日期")
    private String payTime;

    @TableField("state")
    @Property(value = "单据状态", enumClass = ShopMealOrderState.class)
    private Integer state;

    @TableField("type")
    @ApiModelProperty(value = "订单来源，参考#ShopMealOrderType", required = "required,num")
    private Integer type;

    @TableField("store_id")
    @ApiModelProperty(value = "门店ID")
    private String storeId;

    @TableField(exist = false)
    @Property(value = "门店信息")
    private ShopStore storeMation;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("whether_give")
    @ApiModelProperty(value = "是否赠送，参考#WhetherEnum", required = "required,num")
    private Integer whetherGive;

    @TableField("nature_id")
    @ApiModelProperty(value = "套餐订单性质id，参考数据字典")
    private String natureId;

    @TableField(exist = false)
    @Property(value = "套餐订单性质信息")
    private Map<String, Object> natureMation;

    @TableField(exist = false)
    @ApiModelProperty(value = "所购买的套餐信息", required = "required")
    private List<MealOrderChild> mealList;

}
