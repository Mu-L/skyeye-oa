/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.attr.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AttrDefinitionAttrType
 * @Description: 服务类属性字段类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/12 21:27
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AttrDefinitionAttrType implements SkyeyeEnumClass {

    STRING("String", "文本", true, true),
    INTEGER("Integer", "数字", true, true);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
