/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.enumclass;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CompanyTalkGroupInviteState
 * @Description: 群组邀请状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 16:44
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CompanyTalkGroupInviteState implements SkyeyeEnumClass {
    WAITING_CHECK(0, "等待查看", true, true),
    AGREED(1, "同意进群", true, false),
    REJECTED(2, "拒绝进群", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
