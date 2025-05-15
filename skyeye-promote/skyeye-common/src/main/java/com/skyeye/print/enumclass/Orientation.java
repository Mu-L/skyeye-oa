/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.enumclass;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Orientation
 * @Description: 纸张方向枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 10:48
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Orientation implements SkyeyeEnumClass {

    PORTRAIT("portrait", "竖排", true, true),
    LANDSCAPE("landscape", "横排", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
