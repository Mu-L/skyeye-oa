package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NoticeStateEnum implements SkyeyeEnumClass {

    NOT_READ(1,"未读",true,true),

    READ_ED(2,"已读",true,false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
