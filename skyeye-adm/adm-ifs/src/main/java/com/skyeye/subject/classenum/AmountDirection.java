/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.subject.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AmountDirection
 * @Description: 余额方向的枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 22:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AmountDirection implements SkyeyeEnumClass {

    BORROW(1, "借", true, true),
    LOAN(2, "贷", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
