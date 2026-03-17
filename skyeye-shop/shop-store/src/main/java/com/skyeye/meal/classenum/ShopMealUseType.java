/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc.
 * All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopMealUseType
 * @Description: 套餐使用方式：按次数 / 按年限
 * @author: skyeye云系列--卫志强
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopMealUseType implements SkyeyeEnumClass {

    BY_NUM(1, "按次数", true, true),
    BY_YEAR(2, "按年限", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

