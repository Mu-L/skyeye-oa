/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.enumclass;

import com.itextpdf.kernel.geom.PageSize;
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

    A3("A3_PAGE", "A3纸张", PageSize.A3, PageSize.A3.getWidth(), PageSize.A3.getHeight(), true, false),
    A4("A4_PAGE", "A4纸张", PageSize.A4, PageSize.A4.getWidth(), PageSize.A4.getHeight(), true, true),
    A5("A5_PAGE", "A5纸张", PageSize.A5, PageSize.A5.getWidth(), PageSize.A5.getHeight(), true, false),
    LETTER("LETTER_PAGE", "信纸", PageSize.LETTER, PageSize.LETTER.getWidth(), PageSize.LETTER.getHeight(), true, false),
    CUSTOM("CUSTOM_PAGE", "自定义纸张", PageSize.DEFAULT, 1, 1, true, false);

    private String key;

    private String value;

    private PageSize pageSize;

    private float width;

    private float height;

    private Boolean show;

    private Boolean isDefault;

    public static PageSize getPageSizeByKey(String key) {
        for (PaperSize value : PaperSize.values()) {
            if (value.getKey().equals(key)) {
                return value.getPageSize();
            }
        }
        return null;
    }

}
