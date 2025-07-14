/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: KeepFitOrderState
 * @Description: 保养订单状态枚举类
 * 操作：		保养完成              核销
 * 状态：保养中 ----------》待核销  ----------》已核销
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 19:55
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KeepFitOrderState implements SkyeyeEnumClass {

    NO_PAYING(1, "待支付","orange", true, false),
    PAY(2, "已支付", "green",true, false),
    FIT_COMPLATE(3, "保养完成(待核销)","blue", true, false),
    PAY_VERIFICATION(4, "已核销","gray", true, false);

    private Integer key;

    private String value;

    private String coler;

    private Boolean show;

    private Boolean isDefault;
}
