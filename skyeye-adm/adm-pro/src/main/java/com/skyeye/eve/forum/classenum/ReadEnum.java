/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NotificationTypeEnum
 * @Description: 论坛通知状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/11 13:17
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ReadEnum implements SkyeyeEnumClass {
    NO_READ(1, "未读", true, true),
    READ(2, "已读", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
