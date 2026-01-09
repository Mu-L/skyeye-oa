/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ServiceAssignType
 * @Description: 服务人员指派方式枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ServiceAssignType implements SkyeyeEnumClass {

    MANUAL("manual", "手动指派", true, true),
    AUTO("auto", "自动指派", true, false),
    BY_AREA("byArea", "按区域指派", true, false),
    BY_SKILL("bySkill", "按技能指派", true, false),
    BY_ORDER_TYPE("byOrderType", "按工单类型指派", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

