package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ForumStateEnum implements SkyeyeEnumClass {

    NEW_Built(1, "新建", true, true),

    UP_LINE(2, "上线", true, true),

    DOWN_LINE(3, "下线", true, true),

    IS_DELETE(4, "删除", false, false);


    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
