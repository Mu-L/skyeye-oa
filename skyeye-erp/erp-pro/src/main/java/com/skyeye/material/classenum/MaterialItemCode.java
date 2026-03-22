/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.material.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: MaterialItemCode
 * @Description: 商品条形码开启类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/3 14:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaterialItemCode implements SkyeyeEnumClass {

    DISABLE(0, "禁用", "red", true, true),
    ONE_ITEM_CODE(1, "一物一码", "blue", true, false),
    BY_BATCH(2, "按批次", "green", false, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
