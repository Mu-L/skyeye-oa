/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.overtime.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: OvertimeSettleState
 * @Description: 加班是否计入补休/薪资结算状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OvertimeSoltSettleState implements SkyeyeEnumClass {

    WAIT_STATISTICS(1, "待计入统计", true, false),
    RECORDED_IN_STATISTICS(2, "已计入统计", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
