package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ContentStateEnum implements SkyeyeEnumClass {

    NOT_DELETE(1,"正常",true,true),

    DELETE(2,"删除",true,false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}
