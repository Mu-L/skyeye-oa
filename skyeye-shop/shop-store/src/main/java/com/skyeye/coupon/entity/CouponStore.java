/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.CommonInfo;
import lombok.Data;

/**
 * @ClassName: CouponStore
 * @Description: 门店与优惠券关联表实体类
 * @author: skyeye云系列--卫志强
 * @date: 2025/11/26 9:13
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
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
