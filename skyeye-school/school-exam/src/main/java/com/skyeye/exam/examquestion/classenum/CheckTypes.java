package com.skyeye.exam.examquestion.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CheckTypes implements SkyeyeEnumClass {

    NO("No","无限制", 0,true,false),
    EMAIL("Email","Email", 1,true,false),
    UNSTRCN("UNSTRCN","禁止中文", 2,true,false),
    STRCN("STRCN","仅许中文", 3,true,false),
    NUM("NUM","数值", 4,true,false),
    TELENUM("TELENUM","电话号码", 5,true,false),
    PHONENUM("PHONENUM","手机号码", 6,true,false),
    DATE("DATE","日期", 7,true,false),
    IDENTCODE("IDENTCODE","身份证号", 8,true,false),
    ZIPCODE("ZIPCODE","邮政编码", 9,true,false),
    URL("URL","网址", 10,true,false);

    private String key;

    private String value;

    private Integer index;

    private Boolean show;

    private Boolean isDefault;
}
