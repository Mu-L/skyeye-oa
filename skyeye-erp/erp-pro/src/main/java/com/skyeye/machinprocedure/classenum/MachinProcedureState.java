/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: MachinProcedureState
 * @Description: 加工单子单据工序信息状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 14:51
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MachinProcedureState implements SkyeyeEnumClass {

    WAIT_STARTED(1, "待开工", "red", true, false),
    PARTIAL_COMPLETION(2, "部分完工", "orange", true, false),
    ALL_COMPLETED(3, "全部完工", "green",  true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}
