package com.skyeye.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import lombok.Data;

@Data
@TableName(value = "shop_coupon_store")
@ApiModel(value = "门店与优惠券关联表实体类")
public class CouponStore extends CommonInfo {

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField(value = "store_id")
    @ApiModelProperty(value = "门店id")
    private String storeId;

    @TableField(value = "coupon_id")
    @ApiModelProperty(value = "优惠券id")
    private String couponId;
}
