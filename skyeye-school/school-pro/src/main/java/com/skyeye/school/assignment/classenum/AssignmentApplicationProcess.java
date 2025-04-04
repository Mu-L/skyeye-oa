/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AssignmentApplicationProcess
 * @Description: 作业应用环节枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AssignmentApplicationProcess implements SkyeyeEnumClass {

    BEFORE_CLASS("beforeClass", "课前", true, true),
    IN_CLASS("inClass", "课中", true, false),
    AFTER_CLASS("afterClass", "课后", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
