/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NoticeRealLinesType
 * @Description: 公告上线类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/30 20:08
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NoticeRealLinesType implements SkyeyeEnumClass {

    HAND_MOVEMENT(1, "手动上线", true, false),
    AT_REGULAR_TIME(2, "定时上线", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
