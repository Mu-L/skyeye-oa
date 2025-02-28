/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.enumclass;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CompanyTalkGroupState
 * @Description: 群组状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 15:50
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CompanyTalkGroupState implements SkyeyeEnumClass {
    NORMAL(1, "正常", true, true),
    CLOSED(2, "强制举报关闭", true, false),
    DISSOLVED(3, "解散", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
