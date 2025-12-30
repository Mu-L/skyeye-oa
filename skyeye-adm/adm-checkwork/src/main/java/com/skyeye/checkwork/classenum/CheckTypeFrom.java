/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ChectTypeFrom
 * @Description: 考勤按钮的显示状态的类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CheckTypeFrom implements SkyeyeEnumClass {

    CHECT_BTN_FROM_TIMEID(1, "根据班次id计算考勤按钮的显示状态", false, false),
    CHECT_BTN_FROM_OVERTIME(2, "根据加班日计算考勤按钮的显示状态", false, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
