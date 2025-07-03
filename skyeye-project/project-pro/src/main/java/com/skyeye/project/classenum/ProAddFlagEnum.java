package com.skyeye.project.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ProAddFlagEnum
 * @Description: 成本核算新增标识枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/26 12:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ProAddFlagEnum implements SkyeyeEnumClass {

    SYSTEM_ADD(0, "系统新增", true, true),
    HAND_ADD(1, "手动新增", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
