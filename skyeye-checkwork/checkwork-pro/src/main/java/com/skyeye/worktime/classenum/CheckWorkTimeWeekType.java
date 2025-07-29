/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CheckWorkTimeWeekType
 * @Description: 考勤班次里的具体时间的类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CheckWorkTimeWeekType implements SkyeyeEnumClass {

    DAY(1, "每周的当天都上班", true, false),
    SINGLE_DAY(2, "单周上班，双周休假", true, false),
    DOUBLE(3, "不上班", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
