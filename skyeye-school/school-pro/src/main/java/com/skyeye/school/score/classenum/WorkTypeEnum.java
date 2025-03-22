package com.skyeye.school.score.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum WorkTypeEnum implements SkyeyeEnumClass {

    CUSTOM_PERFORMANCE(0, "自定义成绩", true, true),
    WORK_PERFORMANCE(1, "作业成绩", true, false),
    TEST_PERFORMANCE(2, "测试成绩", true, false),
    INTERACTION_PERFORMANCE(3, "互动答题成绩", true, false),
    USUAL_PERFORMANCE(4, "平时成绩", true, false);

    private Integer  key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
