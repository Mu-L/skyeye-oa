/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: SysEveModelAttrType
 * @Description: 系统模型类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/7 14:30
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SysEveModelAttrType implements SkyeyeEnumClass {

    DEFAULT(1, "系统模板", true, true),
    PERSONAL(2, "个人模板", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getName(Integer type) {
        for (SysEveModelAttrType bean : SysEveModelAttrType.values()) {
            if (type == bean.getKey()) {
                return bean.getValue();
            }
        }
        return StringUtils.EMPTY;
    }
}
