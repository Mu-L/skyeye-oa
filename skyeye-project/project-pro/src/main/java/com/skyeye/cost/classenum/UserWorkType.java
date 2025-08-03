package com.skyeye.cost.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: UserWorkType
 * @Description: 人力成本是否为临时工枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/26 12:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserWorkType implements SkyeyeEnumClass {

    TEMPORARY_WORKER(0, "临时工", true, false),
    NORMAL_WORKER(1, "正式工", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
