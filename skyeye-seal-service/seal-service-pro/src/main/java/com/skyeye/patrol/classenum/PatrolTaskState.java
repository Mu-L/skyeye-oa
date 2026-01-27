/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PatrolTaskState
 * @Description: 巡检任务状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PatrolTaskState implements SkyeyeEnumClass {

    PENDING(1, "待执行", true, true),
    IN_PROGRESS(2, "执行中", true, false),
    COMPLETED(3, "已完成", true, false),
    CANCELLED(4, "已取消", true, false),
    TIMEOUT(5, "已超时", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

