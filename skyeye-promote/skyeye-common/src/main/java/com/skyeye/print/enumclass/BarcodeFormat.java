/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.enumclass;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: BarcodeFormat
 * @Description: 条形码格式枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 10:51
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BarcodeFormat implements SkyeyeEnumClass {

    CODE_39("CODE39", "CODE_39", true, true),
    CODE_128("CODE128", "CODE_128", true, false),
    EAN13("EAN13", "EAN_13", true, false),
    EAN8("EAN8", "EAN_8", true, false),
    UPC("UPC", "UPC", true, false),
    ITF("ITF", "ITF", true, false),
    MSI("MSI", "MSI", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
