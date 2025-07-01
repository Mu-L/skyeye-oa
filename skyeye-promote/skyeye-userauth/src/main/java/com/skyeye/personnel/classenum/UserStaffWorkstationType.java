/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: UserStaffWorkstationType
 * @Description: 员工工种类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/6/30 10:03
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserStaffWorkstationType implements SkyeyeEnumClass {

    CONTRACT_WORKER(1, "合同工", true, true),
    HOURLY_WORKER(2, "小时工", true, false),
    PIECE_WORKER(2, "计件工", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
