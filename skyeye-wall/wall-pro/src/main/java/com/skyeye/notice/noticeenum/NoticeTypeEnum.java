package com.skyeye.notice.noticeenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: NoticeTypeEnum
 * @Description: 通知分类枚举类
 * @author: skyeye云系列--lqy
 * @date: 2024/5/15 8:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NoticeTypeEnum implements SkyeyeEnumClass {

    TYPE_CIRCLE(0, "圈子", true, true),
    TYPE_VIDEO(1, "视频", true, false),
    TYPE_WALL(2, "表白墙", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
