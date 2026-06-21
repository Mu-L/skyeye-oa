/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 保养结果
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaintainResultEnum implements SkyeyeEnumClass {

    FINISHED(1, "已完成", true, false),
    UNFINISHED(2, "未完成", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
