package com.skyeye.receivable.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CrmPayStateEnum
 * @Description: 付款状态枚举类
 * @author: skyeye云系列--lqy
 * @date: 2023/2/25 23:06
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CrmPayStateEnum implements SkyeyeEnumClass {

    PAY_STATE(0, "未付款", true, true),
    PAID_STATE(1, "已付款", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
