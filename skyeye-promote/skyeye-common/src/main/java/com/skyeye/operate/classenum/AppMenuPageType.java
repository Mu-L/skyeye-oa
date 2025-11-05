/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.operate.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AppMenuPageType
 * @Description: 移动端菜单页面类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/29 11:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AppMenuPageType implements SkyeyeEnumClass {

    CUSTOM(1, "自定义页面", true, true),
    LAYOUT(2, "表单布局", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
