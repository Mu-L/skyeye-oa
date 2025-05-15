/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.enumclass;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PaperSize
 * @Description: 纸张大小
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 10:47
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PaperSize implements SkyeyeEnumClass {

    A3("A3_PAGE", "A3纸张", true, false),
    A4("A4_PAGE", "A4纸张", true, true),
    A5("A5_PAGE", "A5纸张", true, false),
    Letter("LETTER", "信纸", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
