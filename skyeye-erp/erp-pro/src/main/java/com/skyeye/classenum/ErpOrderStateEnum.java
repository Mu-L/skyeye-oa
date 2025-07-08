/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.common.enumeration.FlowableStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: ErpOrderStateEnum
 * @Description: ERP单据状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/26 12:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErpOrderStateEnum implements SkyeyeEnumClass {

    PARTIALLY_COMPLETED("partiallyCompleted", "部分完成", "orange", true, false),
    COMPLETED("completed", "已完成", "green", true, false),
    NEED_Out("waitOutDepot", "待出库", "blue", true, false),
    PARTIAL_Out("partOutDepot", "部分出库", "orange", true, false),
    All_Out("allOutDepot", "全部出库", "green", true, false);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

    public static List<Class> dependOnEnum() {
        return Arrays.asList(FlowableStateEnum.class);
    }

}
