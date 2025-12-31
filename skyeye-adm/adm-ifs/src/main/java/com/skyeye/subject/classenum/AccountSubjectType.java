/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.subject.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AccountSubjectType
 * @Description: 会计科目类型的枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 22:10
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AccountSubjectType implements SkyeyeEnumClass {

    PROPERTY(1, "资产", true, true),
    IN_DEBT(2, "负债", true, false),
    RIGHTS_AND_INTERESTS(3, "权益", true, false),
    PRIME_COST(4, "成本", true, false),
    INCREASE_AND_DECREASE(5, "损益", true, false),
    COMMON(6, "共同", true, false),
    OTHER(7, "其他", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
