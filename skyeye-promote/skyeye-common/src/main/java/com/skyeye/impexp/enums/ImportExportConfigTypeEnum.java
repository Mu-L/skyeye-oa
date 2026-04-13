/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ImportExportConfigTypeEnum
 * @Description: 导入导出配置类型
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/13 16:20
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ImportExportConfigTypeEnum implements SkyeyeEnumClass {

    IMPORT(1, "导入配置", "blue", true, true),
    EXPORT(2, "导出配置", "green", true, false);

    private Integer key;
    private String value;
    private String color;
    private Boolean show;
    private Boolean isDefault;

}
