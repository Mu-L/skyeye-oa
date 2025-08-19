/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: MemberPlanetEnterEnum
 * @Description: 成为会员的方式枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 22:05
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MemberPlanetEnterEnum  implements SkyeyeEnumClass {

    SELF_ENTER(1, "自己进入", true, false),
    INVITE_ENTER(2, "邀请加入", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
