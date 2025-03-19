/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personrequire.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.common.enumeration.FlowableStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: PersonRequireStateEnum
 * @Description: 人员需求状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PersonRequireStateEnum implements SkyeyeEnumClass {

    IN_RECRUITMENT("inRecruitment", "招聘中", "blue", true, false),
    END_RECRUITMENT("endRecruitment", "招聘结束", "gray", true, false);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

    public static List<Class> dependOnEnum() {
        return Arrays.asList(FlowableStateEnum.class);
    }

}
