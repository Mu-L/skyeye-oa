/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.interviewee.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: UserTransferType
 * @Description: 员工转岗类型的枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/17 22:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserTransferType implements SkyeyeEnumClass {

    FLAT_TONE(1, "平调", true, false),
    PROMOTION(2, "晋升", true, false),
    DEMOTION(3, "降职", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getNameByType(Integer type) {
        for (UserTransferType bean : UserTransferType.values()) {
            if (type == bean.getKey()) {
                return bean.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

}
