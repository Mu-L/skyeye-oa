package com.skyeye.notice.noticeenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TypeEnum
 * @Description: 通知类型
 * @author: skyeye云系列--lqy
 * @date: 2024/5/15 8:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TypeEnum implements SkyeyeEnumClass {

    COMMENT(0, "评论", true, true),
    LIKE(1, "点赞", true, false),
    SHARE(2, "分享", true, false),;

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
