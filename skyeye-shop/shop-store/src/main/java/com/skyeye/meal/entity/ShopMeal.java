/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.meal.classenum.ShopMealType;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: ShopMeal
 * @Description: 套餐管理实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/5 15:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@RedisCacheField(name = "shop:meal", cacheTime = RedisConstants.THIRTY_DAY_SECONDS)
@TableName(value = "shop_meal")
@ApiModel("套餐实体类")
public class ShopMeal extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "title")
    @ApiModelProperty(value = "套餐名称", required = "required", fuzzyLike = true)
    private String title;

    @TableField(exist = false)
    @Property(value = "套餐名称")
    private String name;

    @TableField(value = "logo")
    @ApiModelProperty(value = "套餐logo", required = "required")
    private String logo;

    @TableField(value = "meal_num")
    @ApiModelProperty(value = "套餐可使用次数/年限", required = "required,num")
    private Integer mealNum;

    @TableField(value = "meal_explain")
    @ApiModelProperty(value = "套餐说明", required = "required", fuzzyLike = true)
    private String mealExplain;

    @TableField(value = "type")
    @ApiModelProperty(value = "套餐分类", enumClass = ShopMealType.class, required = "required,num")
    private Integer type;

    @TableField(value = "enabled")
    @ApiModelProperty(value = "状态", enumClass = EnableEnum.class, required = "required,num")
    private Integer enabled;

    @TableField(value = "price")
    @ApiModelProperty(value = "指导价", required = "required,double")
    private String price;

    @TableField(value = "show_price")
    @ApiModelProperty(value = "展示价", required = "required,double")
    private String showPrice;

    @TableField(value = "low_price")
    @ApiModelProperty(value = "底价", required = "required,double")
    private String lowPrice;

    @TableField(exist = false)
    @ApiModelProperty(value = "套餐耗材列表", required = "json")
    private List<ShopMealConsume> mealConsumeList;

    @TableField(exist = false)
    @ApiModelProperty(value = "套餐所属区域", required = "required,json")
    private List<ShopMealArea> mealAreaList;

}
