/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.common.enumeration.FlowableStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: SupplierContractStateEnum
 * @Description: 供应商合同状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SupplierContractStateEnum implements SkyeyeEnumClass {

    EXECUTING("executing", "执行中", "#FFA07A", true, false),
    CLOSE("close", "关闭", "red", true, false),
    LAY_ASIDE("layAside", "搁置", "orange", true, false);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

    public static List<Class> dependOnEnum() {
        return Arrays.asList(FlowableStateEnum.class);
    }

}
