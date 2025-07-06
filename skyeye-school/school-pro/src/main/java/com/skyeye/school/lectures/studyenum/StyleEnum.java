package com.skyeye.school.lectures.studyenum;


import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NumberCodeEnum
 * @Description: 授课班级学风情况评价类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/11 13:17
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StyleEnum implements SkyeyeEnumClass {

    EXCELLENT("1", "优", true, false),
    GOOD("2", "良", true, false),
    MEDIUM("3", "中等", true, false),
    POOR("4", "较差", true, false);


    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
