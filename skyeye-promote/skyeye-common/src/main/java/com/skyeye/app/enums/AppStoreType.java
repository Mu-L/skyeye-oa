/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.enums;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AppStoreType
 * @Description: 应用商店类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 12:36
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AppStoreType implements SkyeyeEnumClass {

    XIAOMI_APP_STORE("xiaomi", "小米", true, true),
    HUAWEI_APP_STORE("huawei", "华为", true, true),
    OPPO_APP_STORE("oppo", "OPPO", true, true),
    VIVO_APP_STORE("vivo", "VIVO", true, true);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getName(String key) {
        for (AppStoreType bean : AppStoreType.values()) {
            if (StrUtil.equals(key, bean.getKey())) {
                return bean.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

}
