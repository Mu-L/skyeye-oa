/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.constans;

/**
 * @ClassName: PlatformBaseSettingConst
 * @Description: 平台基础信息设置项 key 常量
 * <p>
 * 常量值为 settingData 中各分组下的二级 key，与 {@link com.skyeye.tenant.classenum.PlatformBaseSettingGroup} 配合使用。
 */
public class PlatformBaseSettingConst {

    private PlatformBaseSettingConst() {
    }

    /**
     * 租户计费分组 - 成员席位单价（元/席位）
     */
    public static final String KEY_ACCOUNT_UNIT_PRICE = "accountUnitPrice";

}
