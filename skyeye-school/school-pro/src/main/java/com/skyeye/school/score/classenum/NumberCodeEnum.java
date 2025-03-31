/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NumberCodeEnum
 * @Description: 论坛话题举报类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/11 13:17
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NumberCodeEnum implements SkyeyeEnumClass {

    CUSTOM(0, "自定义成绩", true, true),
    WORK(1, "作业成绩", true, false),
    TEST(2, "测试成绩", true, false),
    INTERACTION(3, "互动答题成绩", true, false),
    USUAL(4, "平时成绩", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
