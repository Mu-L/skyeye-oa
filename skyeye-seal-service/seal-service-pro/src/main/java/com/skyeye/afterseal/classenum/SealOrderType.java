/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: SealOrderType
 * @Description: 工单类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/10 13:23
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SealOrderType implements SkyeyeEnumClass {

    WECHAR_USER(1, "微信用户报单", true, true),
    SYS_USER(2, "系统用户报单", true, false),
    CUSTOMER(3, "客户自行报单", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
